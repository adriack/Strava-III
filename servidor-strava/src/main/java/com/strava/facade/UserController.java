package com.strava.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.strava.dto.LoginDTO;
import com.strava.dto.RegistrationDTO;
import com.strava.dto.ResponseWrapper;
import com.strava.dto.TokenDTO;
import com.strava.dto.UserPhysicalInfoDTO;
import com.strava.service.TokenService;
import com.strava.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService, TokenService tokenService) {
        this.userService = userService;
    }

    @Operation(
        summary = "Register a new user",
        description = "Creates a new user in the system. Validates email and credentials with the selected provider."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"user-id\": \"a574c531-e1f7-491d-b052-3862b9b4f1a8\"\n}"))),
        @ApiResponse(responseCode = "400", description = "Validation error response.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"errors\": {\n    \"password\": \"Password is required.\",\n    \"weight\": \"Weight must be greater than zero.\",\n    \"email\": \"Invalid email format.\"\n  }\n}"))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Invalid credentials.\"\n}"))),
        @ApiResponse(responseCode = "404", description = "Email is not registered with the specified provider.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Email is not registered with the specified provider.\"\n}"))),
        @ApiResponse(responseCode = "409", description = "Email is already registered.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"This email is already registered.\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Unexpected error during registration.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Error communicating with authentication provider for email validation.\"\n}")))
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegistrationDTO user) {
        var response = userService.registerUser(user);
        return response.toResponseEntity();
    }

    @Operation(
        summary = "Login user",
        description = "Authenticates a user and generates a token. Validates credentials with the selected provider."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User logged in successfully.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"token\": \"1735388888499\"\n}"))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Invalid credentials.\"\n}"))),
        @ApiResponse(responseCode = "400", description = "User must be registered first.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"User must be registered first.\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Unexpected error during login.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Unexpected error during login.\"\n}")))
    })
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid LoginDTO login) {
        ResponseWrapper response = userService.loginUser(login);
        return response.toResponseEntity();
    }

    @Operation(
        summary = "Logout user",
        description = "Logs out a user and invalidates their token."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User logged out successfully.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"message\": \"User logged out successfully.\"\n}"))),
        @ApiResponse(responseCode = "401", description = "Invalid token.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Invalid token.\"\n}"))),
        @ApiResponse(responseCode = "400", description = "Authorization header is missing.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"errors\": {\n    \"token\": \"Required request header 'Authorization' with user token is missing\"\n  }\n}"))),
        @ApiResponse(responseCode = "500", description = "Unexpected error during logout.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Unexpected error during logout.\"\n}")))
    })
    @PostMapping("/logout")
    @SecurityRequirement(name = "token")
    public ResponseEntity<?> logoutUser(@Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {
        TokenDTO tokenDTO = new TokenDTO(authorizationHeader);
        ResponseWrapper response = userService.logoutUser(tokenDTO);
        return response.toResponseEntity();
    }

    @Operation(
        summary = "Get user info",
        description = "Retrieves user information based on a token."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User info retrieved successfully.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"id\": \"a574c531-e1f7-491d-b052-3862b9b4f1a8\",\n  \"email\": \"user@example.com\",\n  \"name\": \"John Doe\",\n  \"dateOfBirth\": \"1990-01-01\",\n  \"weight\": 75,\n  \"height\": 175,\n  \"maxHeartRate\": 190,\n  \"restingHeartRate\": 60,\n  \"authProvider\": \"GOOGLE\"\n}"))),
        @ApiResponse(responseCode = "401", description = "Invalid token.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Invalid token.\"\n}"))),
        @ApiResponse(responseCode = "400", description = "Authorization header is missing.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"errors\": {\n    \"token\": \"Required request header 'Authorization' with user token is missing\"\n  }\n}"))),
        @ApiResponse(responseCode = "500", description = "Unexpected error during retrieval.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Unexpected error during retrieval.\"\n}")))
    })
    @GetMapping("/info")
    @SecurityRequirement(name = "token")
    public ResponseEntity<?> getUserInfo(@Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {
        TokenDTO tokenDTO = new TokenDTO(authorizationHeader);
        ResponseWrapper response = userService.getUserInfoFromToken(tokenDTO);
        return response.toResponseEntity();
    }

    @Operation(
        summary = "Update user info",
        description = "Updates the user's name and/or physical information based on the provided token. Null given attributes will not be considered."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User physical info updated successfully.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"message\": \"User info updated successfully.\"\n}"))),
        @ApiResponse(responseCode = "401", description = "Invalid token.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Invalid token.\"\n}"))),
        @ApiResponse(responseCode = "400", description = "Authorization header is missing.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"errors\": {\n    \"token\": \"Required request header 'Authorization' with user token is missing\"\n  }\n}"))),
        @ApiResponse(responseCode = "500", description = "Unexpected error during update.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n  \"error\": \"Unexpected error during update.\"\n}")))
    })
    @PatchMapping("/info")
    @SecurityRequirement(name = "token")
    public ResponseEntity<?> updateUserPhysicalInfo(@Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader, @RequestBody @Valid UserPhysicalInfoDTO userPhysicalInfoDTO) {
        TokenDTO tokenDTO = new TokenDTO(authorizationHeader);
        ResponseWrapper response = userService.updateUserPhysicalInfo(tokenDTO, userPhysicalInfoDTO);
        return response.toResponseEntity();
    }

}
