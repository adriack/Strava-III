package com.strava.facade;

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
        return response.toResponseEntity();
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
        return response.toResponseEntity();
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
        return response.toResponseEntity();
    }

}
