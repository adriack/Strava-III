package com.cliente.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO for handling user authentication tokens.")
public class TokenDTO {

    @NotBlank(message = "Token is required.")
    @Schema(description = "Authentication token.", example = "1731663162771")
    private String token;

    @JsonCreator
    public TokenDTO(@JsonProperty("token") String token) {
        this.token = token;
    }

    // Getter y Setter
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
