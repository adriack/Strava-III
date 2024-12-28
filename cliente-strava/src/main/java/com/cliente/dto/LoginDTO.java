package com.cliente.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO for user login information.")
public class LoginDTO {

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    @Schema(description = "The user's email address.", example = "user@example.com", required = true)
    private String email;

    @NotBlank(message = "Password is required.")
    @Schema(description = "The user's password.", example = "P@ssw0rd", required = true)
    private String password;

    @JsonCreator
    public LoginDTO(
            @JsonProperty("email") String email,
            @JsonProperty("password") String password) {
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
