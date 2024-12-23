package com.strava.facade;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.strava.dto.ChallengeDTO;
import com.strava.dto.ChallengeFilterDTO;
import com.strava.dto.ResponseWrapper;
import com.strava.dto.TokenDTO;
import com.strava.entity.enumeration.SportType;
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

        return ResponseEntity.status(HttpStatus.valueOf(response.getStatusCode())).body(response);
    }

    @Operation(summary = "Get active challenges", description = "Fetches active challenges based on filters like date and sport.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Challenges retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Bad Request - Invalid date format"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token")
    })
    @GetMapping
    public ResponseEntity<?> getActiveChallenges(@RequestParam(required = false) String startDate,
                                                  @RequestParam(required = false) String endDate,
                                                  @RequestParam(required = false) SportType sport,
                                                  @RequestParam(required = false) Integer limit) {
        ChallengeFilterDTO filterDTO = new ChallengeFilterDTO();
        try {
            if (startDate != null) {
                filterDTO.setStartDate(LocalDate.parse(startDate));
            }
            if (endDate != null) {
                filterDTO.setEndDate(LocalDate.parse(endDate));
            }
            filterDTO.setSport(sport);
            filterDTO.setLimit(limit);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid date format."));
        }

        ResponseWrapper<?> response = challengeService.getActiveChallenges(filterDTO);
        return ResponseEntity.status(HttpStatus.valueOf(response.getStatusCode())).body(response);
    }

    @Operation(summary = "Accept a challenge", description = "Accepts a challenge for a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Challenge accepted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token")
    })
    @PostMapping("/{challengeId}/accept")
    @Transactional
    public ResponseEntity<?> acceptChallenge(@PathVariable String challengeId, @RequestParam String token) {
        TokenDTO tokenDTO = new TokenDTO(token);
        ResponseWrapper<String> response = challengeService.acceptChallenge(tokenDTO, challengeId);

        return ResponseEntity.status(HttpStatus.valueOf(response.getStatusCode())).body(response);
    }

    @Operation(summary = "Get accepted challenges", description = "Fetches challenges accepted by a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accepted challenges retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token")
    })
    @GetMapping("/accepted")
    public ResponseEntity<?> getAcceptedChallenges(@RequestParam String token) {
        TokenDTO tokenDTO = new TokenDTO(token);
        ResponseWrapper<?> response = challengeService.getAcceptedChallenges(tokenDTO);

        return ResponseEntity.status(HttpStatus.valueOf(response.getStatusCode())).body(response);
    }
}
