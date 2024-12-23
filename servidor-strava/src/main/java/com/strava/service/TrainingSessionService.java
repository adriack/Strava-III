package com.strava.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.strava.dao.TrainingSessionDAO;
import com.strava.dto.ResponseWrapper;
import com.strava.dto.SessionFilterDTO;
import com.strava.dto.TokenDTO;
import com.strava.dto.TrainingSessionDTO;
import com.strava.entity.TrainingSession;
import com.strava.entity.User;

@Service
public class TrainingSessionService {

    private final TrainingSessionDAO trainingSessionDAO;
    private final UserService userService;

    public TrainingSessionService(TrainingSessionDAO trainingSessionDAO, UserService userService) {
        this.trainingSessionDAO = trainingSessionDAO;
        this.userService = userService;
    }

    // Crear una nueva sesión de entrenamiento
    public ResponseWrapper<String> createSession(TokenDTO tokenDTO, TrainingSessionDTO sessionDTO) {
        // Validar token y obtener el usuario
        User user;
        try {
            user = userService.getUserFromToken(tokenDTO);
        } catch (IllegalArgumentException e) {
            return new ResponseWrapper<>(401, "Invalid token", null);
        }

        // Crear el objeto TrainingSession a partir del DTO
        TrainingSession session = new TrainingSession(sessionDTO);
        session.setUser(user);

        // Guardar la sesión en la base de datos
        trainingSessionDAO.save(session);

        return new ResponseWrapper<>(200, "Training session created successfully with ID: " + session.getId(), null);

    }

    // Obtener sesiones de entrenamiento de un usuario, con filtrado
    public ResponseWrapper<List<TrainingSessionDTO>> getUserSessions(TokenDTO tokenDTO, SessionFilterDTO filterDTO) {
        // Validar token y obtener el usuario
        User user;
        try {
            user = userService.getUserFromToken(tokenDTO);
        } catch (IllegalArgumentException e) {
            return new ResponseWrapper<>(401, "Invalid token", null);
        }

        // Obtener rango de fechas para el filtrado
        LocalDate startDate = filterDTO.getStartDate();
        LocalDate endDate = filterDTO.getEndDate();

        // Establecer valores predeterminados si son null
        if (filterDTO.getLimit() == null) {
            filterDTO.setLimit(5);  // Establecer límite por defecto a 5
        }

        if (endDate == null) {
            filterDTO.setEndDate(LocalDate.now());  // Establecer fecha de fin por defecto a la fecha actual
        }
        
        // Convertir la información de filtro a parámetros compatibles con la consulta
        Pageable pageable = PageRequest.of(0, filterDTO.getLimit());  // Usamos limit en la paginación

        // Consultar las sesiones filtradas desde la base de datos
        Page<TrainingSession> sessionsPage = trainingSessionDAO.findFilteredSessions(user.getId(), startDate, endDate, pageable);

        // Convertir las sesiones a DTOs y devolver el resultado
        List<TrainingSessionDTO> sessionsDTO = sessionsPage.getContent().stream()
                .map(session -> new TrainingSessionDTO(session))
                .collect(Collectors.toList());

        return new ResponseWrapper<>(200, "Sessions retrieved successfully.", sessionsDTO);
    }

}
