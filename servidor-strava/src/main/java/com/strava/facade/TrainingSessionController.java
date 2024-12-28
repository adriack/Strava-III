package com.strava.facade;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import com.strava.dto.FilterDTO;
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
    public ResponseEntity<?> createSession(@RequestParam String token, @RequestBody @Validated(TrainingSessionDTO.PostValidation.class) TrainingSessionDTO session) {
        TokenDTO tokenDTO = new TokenDTO(token);
        var response = trainingSessionService.createSession(tokenDTO, session);
        return response.toResponseEntity();
    }

    @Operation(summary = "Get user training sessions", description = "Fetches training sessions for a user with optional date range and limit.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sessions retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token"),
        @ApiResponse(responseCode = "400", description = "Bad Request - Invalid date format or request")
    })
    @GetMapping
    public ResponseEntity<?> getUserSessions(@RequestParam String token,
                                             @ModelAttribute @Valid FilterDTO filterDTO) {
        TokenDTO tokenDTO = new TokenDTO(token);
        var response = trainingSessionService.getUserSessions(tokenDTO, filterDTO);
        return response.toResponseEntity();
    }

    @Operation(summary = "Delete a training session", description = "Deletes an existing training session for a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Session deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token"),
        @ApiResponse(responseCode = "404", description = "Not Found - Session not found or does not belong to the user")
    })
    @DeleteMapping("/{sessionId}")
    @Transactional
    public ResponseEntity<?> deleteSession(@RequestParam String token, @PathVariable UUID sessionId) {
        TokenDTO tokenDTO = new TokenDTO(token);
        var response = trainingSessionService.deleteSession(tokenDTO, sessionId);
        return response.toResponseEntity();
    }

    @Operation(summary = "Update an existing training session", description = "Updates an existing training session for a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Session updated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token"),
        @ApiResponse(responseCode = "404", description = "Not Found - Session not found or does not belong to the user"),
        @ApiResponse(responseCode = "400", description = "Bad Request - Invalid data provided")
    })
    @PutMapping("/{sessionId}")
    @Transactional
    public ResponseEntity<?> updateSession(@RequestParam String token, @PathVariable UUID sessionId, @RequestBody @Valid TrainingSessionDTO session) {
        TokenDTO tokenDTO = new TokenDTO(token);
        var response = trainingSessionService.updateSession(tokenDTO, sessionId, session);
        return response.toResponseEntity();
    }
}
