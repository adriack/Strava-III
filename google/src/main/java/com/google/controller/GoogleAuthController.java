package com.google.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.dto.LoginDTO;
import com.google.service.GoogleAuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/google")
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;

    public GoogleAuthController(GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }

    @Operation(summary = "Verify if an email is registered")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verification result",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)
    })
    @GetMapping("/verify-email")
    public ResponseEntity<Map<String, Boolean>> verifyEmail(@RequestParam String email) {
        boolean registered = googleAuthService.verifyEmail(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("registered", registered);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Validate email and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Credentials are valid",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Invalid email or password",
                    content = @Content)
    })
    @PostMapping("/validate")
    public ResponseEntity<?> validateEmailAndPassword(@RequestBody LoginDTO loginDTO) {
        return googleAuthService.validateEmailAndPassword(loginDTO);
    }

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Email already registered",
                    content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody LoginDTO loginDTO) {
        return googleAuthService.registerUser(loginDTO);
    }
}
