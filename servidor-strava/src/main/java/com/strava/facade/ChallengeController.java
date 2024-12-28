package com.strava.facade;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.strava.dto.ChallengeDTO;
import com.strava.dto.FilterDTO;
import com.strava.dto.ResponseWrapper;
import com.strava.dto.TokenDTO;
import com.strava.service.ChallengeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @Operation(summary = "Create a new challenge", description = "Creates a new challenge for a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Challenge created successfully"),
        @ApiResponse(responseCode = "400", description = "Bad Request - Invalid data provided"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token")
    })
    @PostMapping
    @Transactional
    public ResponseEntity<?> createChallenge(@RequestParam String token, @RequestBody @Valid ChallengeDTO challengeDTO) {
        TokenDTO tokenDTO = new TokenDTO(token);
        ResponseWrapper<String> response = challengeService.createChallenge(tokenDTO, challengeDTO);
        return response.toResponseEntity();
    }

    @Operation(summary = "Get active challenges", description = "Fetches active challenges based on filters like date and sport.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Challenges retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Bad Request - Invalid date format"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token")
    })
    @GetMapping
    public ResponseEntity<?> getActiveChallenges(@ModelAttribute @Valid FilterDTO filterDTO) {
        ResponseWrapper<?> response = challengeService.getActiveChallenges(filterDTO);
        return response.toResponseEntity();
    }

    @Operation(summary = "Accept a challenge", description = "Accepts a challenge for a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Challenge accepted successfully"),
        @ApiResponse(responseCode = "400", description = "Bad Request - Challenge already accepted or has ended"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token"),
        @ApiResponse(responseCode = "404", description = "Not Found - Challenge not found")
    })
    @PostMapping("/{challengeId}/accept")
    @Transactional
    public ResponseEntity<?> acceptChallenge(@PathVariable UUID challengeId, @RequestParam String token) {
        TokenDTO tokenDTO = new TokenDTO(token);
        ResponseWrapper<String> response = challengeService.acceptChallenge(tokenDTO, challengeId);
        return response.toResponseEntity();
    }

    @Operation(summary = "Get accepted challenges", description = "Fetches challenges accepted by a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accepted challenges retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token")
    })
    @GetMapping("/accepted")
    public ResponseEntity<?> getAcceptedChallenges(@RequestParam String token, @RequestParam(defaultValue = "true") boolean includeProgress) {
        TokenDTO tokenDTO = new TokenDTO(token);
        ResponseWrapper<?> response = challengeService.getAcceptedChallenges(tokenDTO, includeProgress);
        return response.toResponseEntity();
    }

    @Operation(summary = "Get created challenges", description = "Fetches challenges created by a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Created challenges retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Not Found - User not found")
    })
    @GetMapping("/created")
    public ResponseEntity<?> getCreatedChallenges(@RequestParam UUID userId) {
        ResponseWrapper<?> response = challengeService.getCreatedChallenges(userId);
        return response.toResponseEntity();
    }

    @Operation(summary = "Get challenge participants with progress", description = "Fetches participants of a challenge along with their progress.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Participants with progress retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Not Found - Challenge not found")
    })
    @GetMapping("/{challengeId}/participants")
    @Transactional
    public ResponseEntity<?> getChallengeParticipants(@PathVariable UUID challengeId) {
        ResponseWrapper<?> response = challengeService.getChallengeParticipants(challengeId);
        return response.toResponseEntity();
    }

}
