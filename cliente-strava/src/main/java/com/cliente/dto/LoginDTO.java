package com.cliente.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO for user login information.")
public class LoginDTO {

    @NotBlank(message = "El correo electrónico es obligatorio.")
    @Email(message = "Formato de correo electrónico no válido.")
    @Schema(description = "The user's email address.", example = "user@example.com", required = true)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria.")
    @Schema(description = "The user's password.", example = "P@ssw0rd", required = true)
    private String password;

    public LoginDTO() {
        // Constructor vacío
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
