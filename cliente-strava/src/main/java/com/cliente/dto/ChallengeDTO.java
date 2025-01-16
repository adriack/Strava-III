package com.cliente.dto;

import java.time.LocalDate;

import com.cliente.entity.enumeration.ObjectiveType;
import com.cliente.entity.enumeration.SportType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Representa un desafío con sus detalles.")
public class ChallengeDTO {

    // Constructor vacío
    public ChallengeDTO() {
    }

    @Schema(description = "Name of the challenge.", example = "Marathon Training")
    @NotBlank(message = "El nombre es obligatorio.")
    private String name;

    @Schema(description = "Start date of the challenge. Must be in the future or present.", example = "2026-01-01")
    @NotNull(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio no puede estar en el pasado.")
    private LocalDate startDate;

    @Schema(description = "End date of the challenge. Must be after or equal to the start date.", example = "2026-01-31")
    @NotNull(message = "La fecha de finalización es obligatoria.")
    @FutureOrPresent(message = "La fecha de finalización no puede estar en el pasado.")
    private LocalDate endDate;

    @Schema(description = "Objective value for the challenge (e.g., distance in km).", example = "42.2")
    @NotNull(message = "El valor del objetivo debe ser proporcionado.")
    @Positive(message = "El valor del objetivo debe ser mayor que cero.")
    private Double objectiveValue;

    @Schema(description = "Type of the objective for the challenge.", example = "DISTANCIA")
    @NotNull(message = "El tipo de objetivo es obligatorio.")
    private ObjectiveType objectiveType;

    @Schema(description = "Sport type of the challenge.", example = "RUNNING")
    @NotNull(message = "El tipo de deporte es obligatorio.")
    private SportType sport;

    // Getters y Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Double getObjectiveValue() { return objectiveValue; }
    public void setObjectiveValue(Double objectiveValue) { this.objectiveValue = objectiveValue; }

    public ObjectiveType getObjectiveType() { return objectiveType; }
    public void setObjectiveType(ObjectiveType objectiveType) { this.objectiveType = objectiveType; }

    public SportType getSport() { return sport; }
    public void setSport(SportType sport) { this.sport = sport; }

}
