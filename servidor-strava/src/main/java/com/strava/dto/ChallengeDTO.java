package com.strava.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strava.entity.Challenge;
import com.strava.entity.enumeration.ObjectiveType;
import com.strava.entity.enumeration.SportType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

@Schema(description = "Represents a challenge with its details.")
public class ChallengeDTO {

    @Schema(description = "Unique identifier of the challenge.", example = "550e8400-e29b-41d4-a726-446655440000", hidden = true)
    private UUID id;

    @Schema(description = "Name of the challenge.", example = "Marathon Training")
    @NotBlank(message = "Name is required.")
    private String name;

    @Schema(description = "Start date of the challenge. Must be in the past or present.", example = "2024-01-01")
    @NotNull(message = "Start date is required.")
    @PastOrPresent(message = "Start date cannot be in the future.")
    private LocalDate startDate;

    @Schema(description = "End date of the challenge. Must be after or equal to the start date.", example = "2024-01-31")
    @NotNull(message = "End date is required.")
    private LocalDate endDate;

    @Schema(description = "Objective value for the challenge (e.g., distance in km).", example = "42.2")
    @NotNull(message = "Objective value must be provided.")
    @Positive(message = "Objective value must be greater than zero.")
    private Double objectiveValue;

    @Schema(description = "Type of the objective for the challenge.", example = "DISTANCIA")
    @NotNull(message = "Objective type is required.")
    private ObjectiveType objectiveType;

    @Schema(description = "Sport type of the challenge.", example = "RUNNING")
    @NotNull(message = "Sport type is required.")
    private SportType sport;

    // Validación personalizada para comprobar que endDate es posterior o igual a startDate
    @Schema(hidden = true)
    @AssertTrue(message = "End date must be greater than or equal to start date.")
    public boolean isEndDateAfterStartDate() {
        return endDate == null || startDate == null || !endDate.isBefore(startDate);
    }

    @JsonCreator
    public ChallengeDTO(
            @JsonProperty("id") UUID id,
            @JsonProperty("name") String name,
            @JsonProperty("startDate") LocalDate startDate,
            @JsonProperty("endDate") LocalDate endDate,
            @JsonProperty("objectiveValue") Double objectiveValue,
            @JsonProperty("objectiveType") ObjectiveType objectiveType,
            @JsonProperty("sport") SportType sport) {

        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.objectiveValue = objectiveValue;
        this.objectiveType = objectiveType;
        this.sport = sport;
    }

    // Constructor que recibe un Challenge y genera un ChallengeDTO
    public ChallengeDTO(Challenge challenge) {
        this.id = challenge.getId();  // Usamos el id del Challenge
        this.name = challenge.getName();
        this.startDate = challenge.getStartDate();
        this.endDate = challenge.getEndDate();
        this.objectiveValue = challenge.getObjectiveValue();
        this.objectiveType = challenge.getObjectiveType();
        this.sport = challenge.getSport();
    }

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Double getObjectiveValue() { return objectiveValue; }
    public void setObjectiveValue(Double objectiveValue) { this.objectiveValue = objectiveValue; }

    public ObjectiveType getObjectiveType() { return objectiveType; }
    public void setObjectiveType(ObjectiveType objectiveType) { this.objectiveType = objectiveType; }

    public SportType getSport() { return sport; }
    public void setSport(SportType sport) { this.sport = sport; }
}
