package com.strava.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strava.entity.enumeration.AuthProvider;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;

@Schema(description = "DTO for user registration and data.")
public class RegistrationDTO {

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    @Schema(description = "User's email address.", example = "user@example.com")
    private String email;

    @NotBlank(message = "Password is required.")
    @Schema(description = "User's password.", example = "SecurePassword123!")
    private String password;

    @NotBlank(message = "Name is required.")
    @Schema(description = "User's full name.", example = "John Doe")
    private String name;

    @NotNull(message = "Date of Birth is required.")
    @Past(message = "Date of Birth must be in the past.")
    @Schema(description = "User's date of birth.", example = "1990-01-15")
    private LocalDate dateOfBirth;

    @Positive(message = "Weight must be greater than zero.")
    @Schema(description = "User's weight in kilograms.", example = "70.5")
    private Double weight;

    @Positive(message = "Height must be greater than zero.")
    @Schema(description = "User's height in meters.", example = "1.75")
    private Double height;

    @Positive(message = "MaxHeartRate must be greater than zero.")
    @Schema(description = "User's maximum heart rate.", example = "190")
    private Integer maxHeartRate;

    @Positive(message = "RestingHeartRate must be greater than zero.")
    @Schema(description = "User's resting heart rate.", example = "60")
    private Integer restingHeartRate;

    @NotNull(message = "AuthProvider is required.")
    @Schema(description = "Authentication provider used by the user.", example = "GOOGLE")
    private AuthProvider authProvider;

    @JsonCreator
    public RegistrationDTO(
            @JsonProperty("email") String email,
            @JsonProperty("name") String name,
            @JsonProperty("dateOfBirth") LocalDate dateOfBirth,
            @JsonProperty("authProvider") AuthProvider authProvider,
            @JsonProperty("password") String password,
            @JsonProperty("weight") Double weight,
            @JsonProperty("height") Double height,
            @JsonProperty("maxHeartRate") Integer maxHeartRate,
            @JsonProperty("restingHeartRate") Integer restingHeartRate) {

        this.email = email;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.authProvider = authProvider;
        this.password = password;
        this.weight = weight;
        this.height = height;
        this.maxHeartRate = maxHeartRate;
        this.restingHeartRate = restingHeartRate;
    }

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
