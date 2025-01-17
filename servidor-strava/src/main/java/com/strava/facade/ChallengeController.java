package com.strava.facade;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.strava.dto.ChallengeDTO;
import com.strava.dto.FilterDTO;
import com.strava.dto.ResponseWrapper;
import com.strava.dto.TokenDTO;
import com.strava.service.ChallengeService;
import com.strava.service.TokenService;

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
@RequestMapping("/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService, TokenService tokenService) {
        this.challengeService = challengeService;
    }

    @Operation(summary = "Create a new challenge", description = "Creates a new challenge for a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Challenge created successfully",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"challenge-id\": \"a574c531-e1f7-491d-b052-3862b9b4f1a8\"\n}"))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(value = "{\n  \"errors\": {\n    \"startDate\": \"Start date cannot be in the future.\"\n  }\n}")
            })),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Invalid token.\"\n}")))
    })
    @PostMapping
    @Transactional
    @SecurityRequirement(name = "token")
    public ResponseEntity<?> createChallenge(@Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader, @RequestBody @Valid ChallengeDTO challengeDTO) {
        TokenDTO tokenDTO = new TokenDTO(authorizationHeader);
        ResponseWrapper response = challengeService.createChallenge(tokenDTO, challengeDTO);
        return response.toResponseEntity();
    }

    @Operation(summary = "Get challenges", description = "Fetches challenges based on filters like date and sport. Active challenges are retrieved by default")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Challenges retrieved successfully",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"challenges\": [\n    {\n      \"id\": \"94d6a98a-6594-4c31-825b-de588f701a47\",\n      \"name\": \"Marathon Training\",\n      \"startDate\": \"2024-01-01\",\n      \"endDate\": \"2024-01-31\",\n      \"objectiveValue\": 42.2,\n      \"objectiveType\": \"DISTANCIA\",\n      \"sport\": \"RUNNING\",\n      \"creatorId\": \"94d6a98a-6594-4c31-825b-de588f701a47\"\n    },\n    {\n      \"id\": \"94d6a98a-6594-4c31-825b-de588f701a48\",\n      \"name\": \"Half Marathon Challenge\",\n      \"startDate\": \"2024-02-01\",\n      \"endDate\": \"2024-02-15\",\n      \"objectiveValue\": 21.1,\n      \"objectiveType\": \"DISTANCIA\",\n      \"sport\": \"RUNNING\",\n      \"creatorId\": \"94d6a98a-6594-4c31-825b-de588f701a47\"\n    }\n  ]\n}"))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(value = "{\n  \"errors\": {\n    \"limit\": \"Limit must be a positive number.\"\n  }\n}")
            })),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Invalid token.\"\n}")))
    })
    @GetMapping
    public ResponseEntity<?> getActiveChallenges(@ModelAttribute @Valid FilterDTO filterDTO) {
        ResponseWrapper response = challengeService.getActiveChallenges(filterDTO);
        return response.toResponseEntity();
    }

    @Operation(summary = "Accept a challenge", description = "Accepts a challenge for a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Challenge accepted successfully",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"message\": \"Challenge accepted.\"\n}"))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(value = "{\n  \"errors\": {\n    \"ended\": \"The challenge has already ended.\"\n  }\n}")
            })),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Invalid token.\"\n}"))),
        @ApiResponse(responseCode = "404", description = "Not Found - Challenge not found",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Challenge not found.\"\n}"))),
        @ApiResponse(responseCode = "409", description = "Conflict - Challenge already accepted or has ended",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Challenge already accepted or has ended.\"\n}")))
    })
    @PostMapping("/{challengeId}/accept")
    @Transactional
    @SecurityRequirement(name = "token")
    public ResponseEntity<?> acceptChallenge(@PathVariable UUID challengeId, @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {
        TokenDTO tokenDTO = new TokenDTO(authorizationHeader);
        ResponseWrapper response = challengeService.acceptChallenge(tokenDTO, challengeId);
        return response.toResponseEntity();
    }

    @Operation(summary = "Get accepted challenges", description = "Fetches challenges accepted by a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accepted challenges retrieved successfully",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"challenges\": [\n    {\n      \"id\": \"94d6a98a-6594-4c31-825b-de588f701a41\",\n      \"name\": \"Marathon Training\",\n      \"startDate\": \"2024-01-01\",\n      \"endDate\": \"2024-01-31\",\n      \"objectiveValue\": 42.2,\n      \"objectiveType\": \"DISTANCIA\",\n      \"sport\": \"RUNNING\",\n      \"creatorId\": \"94d6a98a-6594-4c31-825b-de588f701a47\",\n      \"progress\": 0\n    },\n    {\n      \"id\": \"94d6a98a-6594-4c31-825b-de588f701a48\",\n      \"name\": \"Half Marathon Challenge\",\n      \"startDate\": \"2024-02-01\",\n      \"endDate\": \"2024-02-15\",\n      \"objectiveValue\": 21.1,\n      \"objectiveType\": \"DISTANCIA\",\n      \"sport\": \"RUNNING\",\n      \"creatorId\": \"94d6a98a-6594-4c31-825b-de588f701a47\",\n      \"progress\": 10\n    }\n  ]\n}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Invalid token.\"\n}"))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(value = "{\n  \"errors\": {\n    \"token\": \"Required request header 'Authorization' with user token is missing\"\n  }\n}")
            }))
    })
    @GetMapping("/accepted")
    @SecurityRequirement(name = "token")
    public ResponseEntity<?> getAcceptedChallenges(@Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader, @RequestParam(defaultValue = "true") boolean includeProgress) {
        TokenDTO tokenDTO = new TokenDTO(authorizationHeader);
        ResponseWrapper response = challengeService.getAcceptedChallenges(tokenDTO, includeProgress);
        return response.toResponseEntity();
    }

    @Operation(summary = "Get challenges created by a user", description = "Fetches challenges created by a specific user from user ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Created challenges retrieved successfully",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"challenges\": [\n    {\n      \"id\": \"94d6a98a-6594-4c31-825b-de588f701a42\",\n      \"name\": \"Marathon Training\",\n      \"startDate\": \"2024-01-01\",\n      \"endDate\": \"2024-01-31\",\n      \"objectiveValue\": 42.2,\n      \"objectiveType\": \"DISTANCIA\",\n      \"sport\": \"RUNNING\",\n      \"creatorId\": \"94d6a98a-6594-4c31-825b-de588f701a47\"\n    },\n    {\n      \"id\": \"94d6a98a-6594-4c31-825b-de588f701a48\",\n      \"name\": \"Half Marathon Challenge\",\n      \"startDate\": \"2024-02-01\",\n      \"endDate\": \"2024-02-15\",\n      \"objectiveValue\": 21.1,\n      \"objectiveType\": \"DISTANCIA\",\n      \"sport\": \"RUNNING\",\n      \"creatorId\": \"94d6a98a-6594-4c31-825b-de588f701a47\"\n    }\n  ]\n}"))),
        @ApiResponse(responseCode = "404", description = "Not Found - User not found",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"User not found.\"\n}")))
    })
    @GetMapping("/created")
    public ResponseEntity<?> getCreatedChallenges(@RequestParam UUID userId) {
        ResponseWrapper response = challengeService.getCreatedChallenges(userId);
        return response.toResponseEntity();
    }

    @Operation(summary = "Get challenge participants with progress", description = "Fetches participants of a challenge along with their progress.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Participants with progress retrieved successfully",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"participants\": [\n    {\n      \"userId\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\",\n      \"userName\": \"John Doe\",\n      \"progress\": 75.5\n    },\n    {\n      \"userId\": \"94d6a98a-6594-4c31-825b-de588f701a47\",\n      \"userName\": \"Jane Smith\",\n      \"progress\": 50.0\n    }\n  ]\n}"))),
        @ApiResponse(responseCode = "404", description = "Not Found - Challenge not found",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Challenge not found.\"\n}")))
    })
    @GetMapping("/{challengeId}/participants")
    @Transactional
    public ResponseEntity<?> getChallengeParticipants(@PathVariable UUID challengeId) {
        ResponseWrapper response = challengeService.getChallengeParticipants(challengeId);
        return response.toResponseEntity();
    }

    @Operation(summary = "Get challenge by ID", description = "Fetches a challenge by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Challenge retrieved successfully",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n    \"id\": \"94d6a98a-6594-4c31-825b-de588f701a47\",\n    \"name\": \"Marathon Training\",\n    \"startDate\": \"2024-01-01\",\n    \"endDate\": \"2024-01-31\",\n    \"objectiveValue\": 42.2,\n    \"objectiveType\": \"DISTANCIA\",\n    \"sport\": \"RUNNING\",\n    \"creatorId\": \"94d6a98a-6594-4c31-825b-de588f701a47\"\n  }"))),
        @ApiResponse(responseCode = "404", description = "Not Found - Challenge not found",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Challenge not found.\"\n}")))
    })
    @GetMapping("/{challengeId}")
    public ResponseEntity<?> getChallengeById(@PathVariable UUID challengeId) {
        ResponseWrapper response = challengeService.getChallengeById(challengeId);
        return response.toResponseEntity();
    }

    @Operation(summary = "Check if a user has accepted a challenge", description = "Checks if a user has accepted a specific challenge.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check completed successfully",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"isAccepted\": true\n}"))),
        @ApiResponse(responseCode = "404", description = "Not Found - User or Challenge not found",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"User not found.\"\n}")))
    })
    @Transactional
    @GetMapping("/{challengeId}/isAccepted")
    public ResponseEntity<?> isChallengeAcceptedByUser(@PathVariable UUID challengeId, @RequestParam UUID userId) {
        ResponseWrapper response = challengeService.isChallengeAcceptedByUser(userId, challengeId);
        return response.toResponseEntity();
    }

}
