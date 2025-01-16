package com.cliente.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;

@Schema(description = "DTO for updating user physical information.")
public class UserPhysicalInfoDTO {

    @Schema(description = "User's full name.", example = "John Doe")
    private String name;

    @Past(message = "La fecha de nacimiento debe estar en el pasado.")
    @Schema(description = "User's date of birth.", example = "1990-01-15")
    private LocalDate dateOfBirth;

    @Positive(message = "El peso debe ser mayor que cero.")
    @Schema(description = "User's weight in kilograms.", example = "70.5")
    private Double weight;

    @Positive(message = "La altura debe ser mayor que cero.")
    @Schema(description = "User's height in meters.", example = "1.75")
    private Double height;

    @Positive(message = "La frecuencia cardíaca máxima debe ser mayor que cero.")
    @Schema(description = "User's maximum heart rate.", example = "190")
    private Integer maxHeartRate;

    @Positive(message = "La frecuencia cardíaca en reposo debe ser mayor que cero.")
    @Schema(description = "User's resting heart rate.", example = "60")
    private Integer restingHeartRate;

    // Constructor vacío
    public UserPhysicalInfoDTO() {}

    @Override
    public String toString() {
        return "UserPhysicalInfoDTO{" +
                "weight=" + weight +
                ", height=" + height +
                ", restingHeartRate=" + restingHeartRate +
                ", maxHeartRate=" + maxHeartRate +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }

    // Getters y Setters
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
}