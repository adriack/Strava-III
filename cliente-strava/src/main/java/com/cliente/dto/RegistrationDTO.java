package com.cliente.dto;

import java.time.LocalDate;

import com.cliente.entity.enumeration.AuthProvider;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;

@Schema(description = "DTO for user registration and data.")
public class RegistrationDTO {

    @NotBlank(message = "El correo electrónico es obligatorio.")
    @Email(message = "Formato de correo electrónico no válido.")
    @Schema(description = "Dirección de correo electrónico del usuario.", example = "usuario@ejemplo.com")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria.")
    @Schema(description = "Contraseña del usuario.", example = "ContraseñaSegura123!")
    private String password;

    @NotBlank(message = "El nombre es obligatorio.")
    @Schema(description = "Nombre completo del usuario.", example = "Juan Pérez")
    private String name;

    @NotNull(message = "La fecha de nacimiento es obligatoria.")
    @Past(message = "La fecha de nacimiento debe estar en el pasado.")
    @Schema(description = "Fecha de nacimiento del usuario.", example = "1990-01-15")
    private LocalDate dateOfBirth;

    @Positive(message = "El peso debe ser mayor que cero.")
    @Schema(description = "Peso del usuario en kilogramos.", example = "70.5")
    private Double weight;

    @Positive(message = "La altura debe ser mayor que cero.")
    @Schema(description = "Altura del usuario en metros.", example = "1.75")
    private Double height;

    @Positive(message = "La frecuencia cardíaca máxima debe ser mayor que cero.")
    @Schema(description = "Frecuencia cardíaca máxima del usuario.", example = "190")
    private Integer maxHeartRate;

    @Positive(message = "La frecuencia cardíaca en reposo debe ser mayor que cero.")
    @Schema(description = "Frecuencia cardíaca en reposo del usuario.", example = "60")
    private Integer restingHeartRate;

    @NotNull(message = "El proveedor de autenticación es obligatorio.")
    @Schema(description = "Proveedor de autenticación utilizado por el usuario.", example = "GOOGLE")
    private AuthProvider authProvider;

    // Constructor vacío
    public RegistrationDTO() {}

    // Getters y Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public Integer getMaxHeartRate() { return maxHeartRate; }
    public void setMaxHeartRate(Integer maxHeartRate) { this.maxHeartRate = maxHeartRate; }

    public Integer getRestingHeartRate() { return restingHeartRate; }
    public void setRestingHeartRate(Integer restingHeartRate) { this.restingHeartRate = restingHeartRate; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public AuthProvider getAuthProvider() { return authProvider; }
    public void setAuthProvider(AuthProvider authProvider) { this.authProvider = authProvider; }
}
