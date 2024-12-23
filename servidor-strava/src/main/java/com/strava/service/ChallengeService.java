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
import com.strava.dto.ChallengeDTO;
import com.strava.dto.ChallengeFilterDTO;
import com.strava.dto.ResponseWrapper;
import com.strava.dto.TokenDTO;
import com.strava.entity.Challenge;
import com.strava.entity.User;
import com.strava.entity.enumeration.SportType;

@Service
public class ChallengeService {

    private final UserService userService;
    private final ChallengeDAO challengeDAO;

    public ChallengeService(UserService userService, ChallengeDAO challengeDAO) {
        this.userService = userService;
        this.challengeDAO = challengeDAO;
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
        challenge.addUser(user); // Asociar el usuario al reto

        // Guardar el reto en la base de datos
        challengeDAO.save(challenge);

        return new ResponseWrapper<>(200, "Challenge created successfully with ID: " + challenge.getId(), null);
    }

    public ResponseWrapper<List<ChallengeDTO>> getActiveChallenges(ChallengeFilterDTO challengeFilterDTO) {
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
    public ResponseWrapper<String> acceptChallenge(TokenDTO tokenDTO, String challengeId) {
        // Validar token y obtener el usuario
        User user;
        try {
            user = userService.getUserFromToken(tokenDTO);
        } catch (IllegalArgumentException e) {
            return new ResponseWrapper<>(401, "Invalid token", null);
        }

        // Convertir el ID del reto a UUID y buscar el reto
        UUID challengeUUID = UUID.fromString(challengeId);
        Challenge challenge = challengeDAO.findById(challengeUUID)
                .orElse(null);

        if (challenge == null) {
            return new ResponseWrapper<>(404, "Challenge not found.", null);
        }

        // Asociación del reto y el usuario
        challenge.addUser(user);

        // Guardar los cambios en la base de datos
        challengeDAO.save(challenge);

        return new ResponseWrapper<>(200, "Challenge accepted.", null);
    }

    // Obtener todos los retos aceptados por el usuario
    public ResponseWrapper<List<ChallengeDTO>> getAcceptedChallenges(TokenDTO tokenDTO) {
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

        // Convertir las entidades de Challenge a DTOs
        List<ChallengeDTO> challenges = challengesPage.getContent().stream()
                .map(challenge -> new ChallengeDTO(challenge))
                .collect(Collectors.toList());

        return new ResponseWrapper<>(200, "Accepted challenges retrieved successfully.", challenges);
    }

}

