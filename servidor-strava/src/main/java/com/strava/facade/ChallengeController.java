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
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"challenge-id\": \"a574c531-e1f7-491d-b052-3862b9b4f1a8\"}"))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(value = "{\n  \"errors\": {\n    \"startDate\": \"Start date cannot be in the future.\"}"),
            })),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Invalid token.\"}")))
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
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"challenges\": [{\"id\": \"94d6a98a-6594-4c31-825b-de588f701a47\", \"name\": \"Marathon Training\", \"startDate\": \"2024-01-01\", \"endDate\": \"2024-01-31\", \"objectiveValue\": 42.2, \"objectiveType\": \"DISTANCIA\", \"sport\": \"RUNNING\", \"creatorId\": \"94d6a98a-6594-4c31-825b-de588f701a47\"}, {\"id\": \"94d6a98a-6594-4c31-825b-de588f701a48\", \"name\": \"Half Marathon Challenge\", \"startDate\": \"2024-02-01\", \"endDate\": \"2024-02-15\", \"objectiveValue\": 21.1, \"objectiveType\": \"DISTANCIA\", \"sport\": \"RUNNING\", \"creatorId\": \"94d6a98a-6594-4c31-825b-de588f701a47\"}]}"
            ))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(value = "{\n  \"errors\": {\n    \"limit\": \"Limit must be a positive number.\"}"),
            })),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Invalid token.\"}")))
    })
    @GetMapping
    public ResponseEntity<?> getActiveChallenges(@ModelAttribute @Valid FilterDTO filterDTO) {
        ResponseWrapper response = challengeService.getActiveChallenges(filterDTO);
        return response.toResponseEntity();
    }

    @Operation(summary = "Accept a challenge", description = "Accepts a challenge for a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Challenge accepted successfully",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Challenge accepted.\"}"))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(value = "{\n  \"errors\": {\n    \"ended\": \"The challenge has already ended.\"\n  }\n}")
            })),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Invalid token.\"}"))),
        @ApiResponse(responseCode = "404", description = "Not Found - Challenge not found",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Challenge not found.\"}"))),
        @ApiResponse(responseCode = "409", description = "Conflict - Challenge already accepted or has ended",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Challenge already accepted or has ended.\"}")))
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
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"challenges\": [{\"id\": \"94d6a98a-6594-4c31-825b-de588f701a41\", \"name\": \"Marathon Training\", \"startDate\": \"2024-01-01\", \"endDate\": \"2024-01-31\", \"objectiveValue\": 42.2, \"objectiveType\": \"DISTANCIA\", \"sport\": \"RUNNING\", \"creatorId\": \"94d6a98a-6594-4c31-825b-de588f701a47\", \"progress\": 0}, {\"id\": \"94d6a98a-6594-4c31-825b-de588f701a48\", \"name\": \"Half Marathon Challenge\", \"startDate\": \"2024-02-01\", \"endDate\": \"2024-02-15\", \"objectiveValue\": 21.1, \"objectiveType\": \"DISTANCIA\", \"sport\": \"RUNNING\", \"creatorId\": \"94d6a98a-6594-4c31-825b-de588f701a47\", \"progress\": 10}]}"
            ))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Invalid token.\"}"))),
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
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"challenges\": [{\"id\": \"94d6a98a-6594-4c31-825b-de588f701a42\", \"name\": \"Marathon Training\", \"startDate\": \"2024-01-01\", \"endDate\": \"2024-01-31\", \"objectiveValue\": 42.2, \"objectiveType\": \"DISTANCIA\", \"sport\": \"RUNNING\", \"creatorId\": \"94d6a98a-6594-4c31-825b-de588f701a47\"}, {\"id\": \"94d6a98a-6594-4c31-825b-de588f701a48\", \"name\": \"Half Marathon Challenge\", \"startDate\": \"2024-02-01\", \"endDate\": \"2024-02-15\", \"objectiveValue\": 21.1, \"objectiveType\": \"DISTANCIA\", \"sport\": \"RUNNING\", \"creatorId\": \"94d6a98a-6594-4c31-825b-de588f701a47\"}]}"
            ))),
        @ApiResponse(responseCode = "404", description = "Not Found - User not found",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"User not found.\"}")))
    })
    @GetMapping("/created")
    public ResponseEntity<?> getCreatedChallenges(@RequestParam UUID userId) {
        ResponseWrapper response = challengeService.getCreatedChallenges(userId);
        return response.toResponseEntity();
    }

    @Operation(summary = "Get challenge participants with progress", description = "Fetches participants of a challenge along with their progress.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Participants with progress retrieved successfully",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"participants\": [{\"userId\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\", \"userName\": \"John Doe\", \"progress\": 75.5}, {\"userId\": \"94d6a98a-6594-4c31-825b-de588f701a47\", \"userName\": \"Jane Smith\", \"progress\": 50.0}]}"
            ))),
        @ApiResponse(responseCode = "404", description = "Not Found - Challenge not found",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Challenge not found.\"}")))
    })
    @GetMapping("/{challengeId}/participants")
    @Transactional
    public ResponseEntity<?> getChallengeParticipants(@PathVariable UUID challengeId) {
        ResponseWrapper response = challengeService.getChallengeParticipants(challengeId);
        return response.toResponseEntity();
    }

}
