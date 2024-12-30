package com.strava.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for handling user authentication tokens.")
public class TokenDTO {

    @Schema(description = "Authentication token.", example = "1731663162771")
    private String token;

    public TokenDTO(String token) {
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
