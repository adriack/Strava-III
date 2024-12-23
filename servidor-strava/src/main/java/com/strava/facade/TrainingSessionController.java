package com.strava.facade;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.strava.dto.SessionFilterDTO;
import com.strava.dto.TokenDTO;
import com.strava.dto.TrainingSessionDTO;
import com.strava.service.TrainingSessionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/sessions")
public class TrainingSessionController {

    private final TrainingSessionService trainingSessionService;

    public TrainingSessionController(TrainingSessionService trainingSessionService) {
        this.trainingSessionService = trainingSessionService;
    }

    @Operation(summary = "Create a new training session", description = "Creates a new training session for a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Session created successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token"),
        @ApiResponse(responseCode = "400", description = "Bad Request - Invalid data provided")
    })
    @PostMapping
    @Transactional
    public ResponseEntity<?> createSession(@RequestParam String token, @RequestBody @Valid TrainingSessionDTO session) {
        TokenDTO tokenDTO = new TokenDTO(token);
        var response = trainingSessionService.createSession(tokenDTO, session);

        switch (response.getStatusCode()) {
            case 200 -> {
                return ResponseEntity.ok(response.getMessage());
            }
            case 401 -> {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response.getMessage());
            }
            case 400 -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.getMessage());
            }
            default -> {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error during session creation.");
            }
        }
    }

    @Operation(summary = "Get user training sessions", description = "Fetches training sessions for a user with optional date range and limit.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sessions retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token"),
        @ApiResponse(responseCode = "400", description = "Bad Request - Invalid date format or request")
    })
    @GetMapping
    public ResponseEntity<?> getUserSessions(@RequestParam String token,
                                             @RequestParam(required = false) String startDate,
                                             @RequestParam(required = false) String endDate,
                                             @RequestParam(required = false) Integer limit) {
        TokenDTO tokenDTO = new TokenDTO(token);
        SessionFilterDTO filterDTO = new SessionFilterDTO();

        try {
            filterDTO.setStartDate(startDate != null ? LocalDate.parse(startDate) : null);
            filterDTO.setEndDate(endDate != null ? LocalDate.parse(endDate) : null);
            filterDTO.setLimit(limit);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format.");
        }

        var response = trainingSessionService.getUserSessions(tokenDTO, filterDTO);

        switch (response.getStatusCode()) {
            case 200 -> {
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("sessions", response.getData());
                responseMap.put("message", response.getMessage());
                return ResponseEntity.ok(responseMap);
            }
            case 401 -> {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response.getMessage());
            }
            case 400 -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.getMessage());
            }
            default -> {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error during session retrieval.");
            }
        }
    }
}
