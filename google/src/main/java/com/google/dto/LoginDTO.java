package com.google.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;


public class LoginDTO {

    @Schema(description = "The user's email address.", example = "user@gmail.com", required = true)
    private String email;

    @Schema(description = "The user's password.", example = "P@ssw0rd", required = true)
    private String password;

    @JsonCreator
    public LoginDTO(
            @JsonProperty("email") String email,
            @JsonProperty("password") String password) {

        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }

        this.email = email;
        this.password = password;
    }

    // Getters y Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
