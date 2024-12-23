package com.strava.facade;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.strava.dto.LoginDTO;
import com.strava.dto.TokenDTO;
import com.strava.dto.UserDTO;
import com.strava.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
        summary = "Register a new user",
        description = "Creates a new user in the system. Validates email and credentials with the selected provider."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully."),
        @ApiResponse(responseCode = "400", description = "Invalid user data or email already exists."),
        @ApiResponse(responseCode = "401", description = "Invalid credentials."),
        @ApiResponse(responseCode = "500", description = "Unexpected error during registration.")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserDTO user) {
        var response = userService.registerUser(user);
        return switch (response.getStatusCode()) {
            case 201 -> ResponseEntity.status(HttpStatus.CREATED).body(response.getMessage());
            case 401 -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response.getMessage());
            case 400 -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.getMessage());
            case 500 -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response.getMessage() != null ? response.getMessage() : "Unexpected error during registration.");
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error during registration.");
        };
    }

    @Operation(
        summary = "Login user",
        description = "Authenticates a user and generates a token. Validates credentials with the selected provider."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User logged in successfully."),
        @ApiResponse(responseCode = "401", description = "Invalid credentials."),
        @ApiResponse(responseCode = "400", description = "User must be registered first."),
        @ApiResponse(responseCode = "500", description = "Unexpected error during login.")
    })
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid LoginDTO login) {
        var response = userService.loginUser(login);
        return switch (response.getStatusCode()) {
            case 200 -> {
                Map<String, String> responseMap = new HashMap<>();
                responseMap.put("message", response.getMessage());
                responseMap.put("token", (String) response.getData());
                yield ResponseEntity.ok(responseMap);
            }
            case 401 -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response.getMessage());
            case 400 -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.getMessage());
            case 500 -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response.getMessage() != null ? response.getMessage() : "Unexpected error during login.");
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error during login.");
        };
    }

    @Operation(
        summary = "Logout user",
        description = "Logs out a user and invalidates their token."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User logged out successfully."),
        @ApiResponse(responseCode = "400", description = "Invalid token."),
        @ApiResponse(responseCode = "500", description = "Unexpected error during logout.")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody @Valid TokenDTO token) {
        var response = userService.logoutUser(token);
        return switch (response.getStatusCode()) {
            case 200 -> ResponseEntity.ok(response.getMessage());
            case 400 -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.getMessage());
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error during logout.");
        };
    }

}
