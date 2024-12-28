package com.strava.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.strava.dao.ChallengeDAO;
import com.strava.dao.TrainingSessionDAO;
import com.strava.dto.ChallengeDTO;
import com.strava.dto.FilterDTO;
import com.strava.dto.ResponseWrapper;
import com.strava.dto.TokenDTO;
import com.strava.dto.UserProgressDTO;
import com.strava.entity.Challenge;
import com.strava.entity.TrainingSession;
import com.strava.entity.User;
import com.strava.entity.enumeration.SportType;

@Service
public class ChallengeService {

    private final UserService userService;
    private final ChallengeDAO challengeDAO;
    private final TrainingSessionDAO trainingSessionDAO;

    public ChallengeService(UserService userService, ChallengeDAO challengeDAO, TrainingSessionDAO trainingSessionDAO) {
        this.userService = userService;
        this.challengeDAO = challengeDAO;
        this.trainingSessionDAO = trainingSessionDAO;
    }

    // Crear un reto y almacenarlo en la base de datos
    public ResponseWrapper<String> createChallenge(TokenDTO tokenDTO, ChallengeDTO challengeDTO) {
        // Validar token y obtener el usuario
        User user;
        try {
            user = userService.getUserFromToken(tokenDTO);
        } catch (IllegalArgumentException e) {
            return new ResponseWrapper<>(401, "Invalid token", null);
        }

        // Crear un nuevo objeto Challenge a partir del DTO
        Challenge challenge = new Challenge(challengeDTO);
        user.addCreatedChallenge(challenge); // Asociar el usuario al reto como creador
        challenge.addUser(user); // Asociar el usuario al reto como particpante

        // Guardar el reto en la base de datos
        challengeDAO.save(challenge);

        return new ResponseWrapper<>(200, "Challenge created successfully.", "ID: " + challenge.getId());
    }

    public ResponseWrapper<List<ChallengeDTO>> getActiveChallenges(FilterDTO challengeFilterDTO) {
        LocalDate startDate = challengeFilterDTO.getStartDate();
        LocalDate endDate = challengeFilterDTO.getEndDate();
        SportType sport = challengeFilterDTO.getSport();
        
        // Establecer un límite por defecto de 5 si el valor de limit es null
        if (challengeFilterDTO.getLimit() == null) {
            challengeFilterDTO.setLimit(5);
        }
    
        Pageable pageable = PageRequest.of(0, challengeFilterDTO.getLimit());
    
        // Consultar los retos filtrados desde la base de datos
        Page<Challenge> challengesPage = challengeDAO.findFilteredChallenges(
                null, startDate, endDate, sport, pageable
        );
    
        // Convertir las entidades de Challenge a DTOs
        List<ChallengeDTO> challenges = challengesPage.getContent().stream()
                .map(challenge -> new ChallengeDTO(challenge))
                .collect(Collectors.toList());

        return new ResponseWrapper<>(200, "Active challenges retrieved successfully.", challenges);
    }    

    // Aceptar un reto y asociarlo al usuario
    public ResponseWrapper<String> acceptChallenge(TokenDTO tokenDTO, UUID challengeId) {
        // Validar token y obtener el usuario
        User user;
        try {
            user = userService.getUserFromToken(tokenDTO);
        } catch (IllegalArgumentException e) {
            return new ResponseWrapper<>(401, "Invalid token", null);
        }

        // Buscar el reto
        Challenge challenge = challengeDAO.findById(challengeId)
            .orElse(null);

        if (challenge == null) {
            return new ResponseWrapper<>(404, "Challenge not found.", null);
        }

        // Verificar si el reto ya ha terminado
        if (challenge.getEndDate().isBefore(LocalDate.now())) {
            return new ResponseWrapper<>(400, "Challenge has already ended.", null);
        }

        // Verificar si el usuario ya ha aceptado el reto
        if (challenge.getUsers().contains(user)) {
            return new ResponseWrapper<>(400, "Challenge already accepted.", null);
        }

        // Asociación del reto y el usuario
        challenge.addUser(user);

        // Guardar los cambios en la base de datos
        challengeDAO.save(challenge);

        return new ResponseWrapper<>(200, "Challenge accepted.", null);
    }

    public ResponseWrapper<List<ChallengeDTO>> getAcceptedChallenges(TokenDTO tokenDTO, boolean includeProgress) {
        // Validar token y obtener el usuario
        User user;
        try {
            user = userService.getUserFromToken(tokenDTO);
        } catch (IllegalArgumentException e) {
            return new ResponseWrapper<>(401, "Invalid token", null);
        }

        // Consultar los retos aceptados desde la base de datos
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);  // Recuperamos todos los retos sin paginación
        Page<Challenge> challengesPage = challengeDAO.findFilteredChallenges(
                user.getId(), null, null, null, pageable
        );

        // Convertir las entidades de Challenge a DTOs y calcular el progreso si es necesario
        List<ChallengeDTO> challenges = challengesPage.getContent().stream()
                .map(challenge -> {
                    ChallengeDTO challengeDTO = new ChallengeDTO(challenge);
                    if (includeProgress) {
                        double progress = calculateProgress(user, challenge);
                        challengeDTO.setProgress(progress);
                    }
                    return challengeDTO;
                })
                .collect(Collectors.toList());

        return new ResponseWrapper<>(200, "Accepted challenges retrieved successfully.", challenges);
    }

    public ResponseWrapper<List<UserProgressDTO>> getChallengeParticipants(UUID challengeId) {
        // Buscar el reto
        Challenge challenge = challengeDAO.findById(challengeId)
            .orElse(null);

        if (challenge == null) {
            return new ResponseWrapper<>(404, "Challenge not found.", null);
        }

        // Obtener los usuarios participantes y sus progresos
        List<UserProgressDTO> userProgressList = challenge.getUsers().stream()
            .map(user -> {
                double progress = calculateProgress(user, challenge);
                return new UserProgressDTO(user.getName(), progress);
            })
            .collect(Collectors.toList());

        return new ResponseWrapper<>(200, "Participants with progress retrieved successfully.", userProgressList);
    }

    public ResponseWrapper<List<ChallengeDTO>> getCreatedChallenges(UUID userId) {
        // Obtener el usuario desde el UserService
        User user = userService.getUserById(userId);
        if (user == null) {
            return new ResponseWrapper<>(404, "User not found.", null);
        }

        // Consultar los retos creados por el usuario desde la base de datos
        List<Challenge> challenges = challengeDAO.findByCreator(user);

        // Convertir las entidades de Challenge a DTOs
        List<ChallengeDTO> challengeDTOs = challenges.stream()
                .map(ChallengeDTO::new)
                .collect(Collectors.toList());

        return new ResponseWrapper<>(200, "Created challenges retrieved successfully.", challengeDTOs);
    }

    private double calculateProgress(User user, Challenge challenge) {
        // Obtener sesiones de entrenamiento del usuario dentro del rango de fechas del reto
        Pageable pageable = Pageable.unpaged(); // Sin límite de sesiones
        Page<TrainingSession> sessionPage = trainingSessionDAO.findFilteredSessions(
            user.getId(), challenge.getStartDate(), challenge.getEndDate(), challenge.getSport(), pageable
        );
        List<TrainingSession> sessions = sessionPage.getContent();

        // Calcular el progreso basado en el tipo de objetivo
        double totalValue = sessions.stream()
            .mapToDouble(session -> switch (challenge.getObjectiveType()) {
                case DISTANCIA -> session.getDistance();
                case TIEMPO -> session.getDuration();
                default -> 0;
            })
            .sum();

        double objectiveValue = challenge.getObjectiveValue();

        return (totalValue / objectiveValue) * 100;
    }

}

