package com.strava.facade;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.strava.dto.FilterDTO;
import com.strava.dto.ResponseWrapper;
import com.strava.dto.TokenDTO;
import com.strava.dto.TrainingSessionDTO;
import com.strava.service.TokenService;
import com.strava.service.TrainingSessionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/sessions")
public class TrainingSessionController {

    private final TrainingSessionService trainingSessionService;

    public TrainingSessionController(TrainingSessionService trainingSessionService, TokenService tokenService) {
        this.trainingSessionService = trainingSessionService;
    }

    @Operation(summary = "Create a new training session", description = "Creates a new training session for a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Session created successfully",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"session-id\": \"a574c531-e1f7-491d-b052-3862b9b4f1a8\"\n}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Invalid token.\"\n}"))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(value = "{\n  \"errors\": {\n    \"distance\": \"Distance must be greater than zero.\"\n  }\n}")
            }))
    })
    @PostMapping
    @SecurityRequirement(name = "token")
    public ResponseEntity<?> createSession(@Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader, @RequestBody @Valid TrainingSessionDTO session) {
        TokenDTO tokenDTO = new TokenDTO(authorizationHeader);
        ResponseWrapper response = trainingSessionService.createSession(tokenDTO, session);
        return response.toResponseEntity();
    }

    @Operation(summary = "Get user training sessions", description = "Fetches training sessions for a user with optional date range and limit.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sessions retrieved successfully",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"sessions\": [\n    {\n      \"id\": \"f8ebbff6-c133-476e-86b5-ed0af7056c0d\",\n      \"title\": \"Morning Run\",\n      \"sport\": \"RUNNING\",\n      \"distance\": 5,\n      \"startDate\": \"2024-12-11\",\n      \"startTime\": \"06:30\",\n      \"duration\": 30\n    },\n    {\n      \"id\": \"f8ebbff6-c133-476e-86b5-ed0af7056c0e\",\n      \"title\": \"Evening Walk\",\n      \"sport\": \"CICLISMO\",\n      \"distance\": 3,\n      \"startDate\": \"2024-12-11\",\n      \"startTime\": \"18:00\",\n      \"duration\": 45\n    }\n  ]\n}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Invalid token.\"\n}"))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(value = "{\n  \"error\": \"Invalid date format or request.\"\n}")
            }))
    })
    @GetMapping
    @SecurityRequirement(name = "token")
    public ResponseEntity<?> getUserSessions(@Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader,
                                             @ModelAttribute @Valid FilterDTO filterDTO) {
        TokenDTO tokenDTO = new TokenDTO(authorizationHeader);
        ResponseWrapper response = trainingSessionService.getUserSessions(tokenDTO, filterDTO);
        return response.toResponseEntity();
    }

    @Operation(summary = "Delete a training session", description = "Deletes an existing training session for a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Session deleted successfully",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"message\": \"Training session deleted successfully.\"\n}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Invalid token.\"\n}"))),
        @ApiResponse(responseCode = "404", description = "Not Found - Session not found or does not belong to the user",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Training session not found or does not belong to the user.\"\n}"))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"errors\": {\n    \"token\": \"Required request header 'Authorization' with user token is missing\"\n  }\n}")))
    })
    @DeleteMapping("/{sessionId}")
    @Transactional
    @SecurityRequirement(name = "token")
    public ResponseEntity<?> deleteSession(@Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader, @PathVariable UUID sessionId) {
        TokenDTO tokenDTO = new TokenDTO(authorizationHeader);
        ResponseWrapper response = trainingSessionService.deleteSession(tokenDTO, sessionId);
        return response.toResponseEntity();
    }

    @Operation(summary = "Update an existing training session", description = "Updates an existing training session for a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Session updated successfully",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"message\": \"Training session updated successfully.\"\n}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Invalid token.\"\n}"))),
        @ApiResponse(responseCode = "404", description = "Not Found - Session not found or does not belong to the user",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Training session not found or does not belong to the user.\"\n}"))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"errors\": {\n    \"token\": \"Required request header 'Authorization' with user token is missing\"\n  }\n}")))
    })
    @PutMapping("/{sessionId}")
    @Transactional
    @SecurityRequirement(name = "token")
    public ResponseEntity<?> updateSession(@Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader, @PathVariable UUID sessionId, @RequestBody @Valid TrainingSessionDTO session) {
        TokenDTO tokenDTO = new TokenDTO(authorizationHeader);
        ResponseWrapper response = trainingSessionService.updateSession(tokenDTO, sessionId, session);
        return response.toResponseEntity();
    }
}
