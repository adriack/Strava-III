package com.strava.dto;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strava.entity.Challenge;
import com.strava.entity.enumeration.ObjectiveType;
import com.strava.entity.enumeration.SportType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Represents a challenge with its details.")
public class ChallengeDTO {

    @Schema(description = "Unique identifier of the challenge.", example = "550e8400-e29b-41d4-a726-446655440000", hidden = true)
    private UUID id;

    @Schema(description = "Name of the challenge.", example = "Marathon Training")
    @NotBlank(message = "Name is required.")
    private String name;

    @Schema(description = "Start date of the challenge. Must be in the future or present.", example = "2026-01-01")
    @NotNull(message = "Start date is required.")
    @FutureOrPresent(message = "Start date cannot be in the past.")
    private LocalDate startDate;

    @Schema(description = "End date of the challenge. Must be after or equal to the start date.", example = "2026-01-31")
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

    @Schema(description = "Progress of the challenge in percentage.", example = "75.0", hidden = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double progress;

    @Schema(description = "ID of the user who created the challenge.", example = "550e8400-e29b-41d4-a726-446655440000", hidden = true)
    private UUID creatorId;

    @JsonCreator
    public ChallengeDTO(
            @JsonProperty("name") String name,
            @JsonProperty("startDate") LocalDate startDate,
            @JsonProperty("endDate") LocalDate endDate,
            @JsonProperty("objectiveValue") Double objectiveValue,
            @JsonProperty("objectiveType") ObjectiveType objectiveType,
            @JsonProperty("sport") SportType sport) {

        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.objectiveValue = objectiveValue;
        this.objectiveType = objectiveType;
        this.sport = sport;
        swapDatesIfNecessary();
    }

    // Constructor que recibe un Challenge y genera un ChallengeDTO
    public ChallengeDTO(Challenge challenge) {
        this.id = challenge.getId();
        this.name = challenge.getName();
        this.startDate = challenge.getStartDate();
        this.endDate = challenge.getEndDate();
        this.objectiveValue = challenge.getObjectiveValue();
        this.objectiveType = challenge.getObjectiveType();
        this.sport = challenge.getSport();
        this.creatorId = challenge.getCreator().getId();
        swapDatesIfNecessary();
    }

    private void swapDatesIfNecessary() {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            LocalDate temp = startDate;
            startDate = endDate;
            endDate = temp;
        }
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

    public Double getProgress() { return progress; }
    public void setProgress(Double progress) { this.progress = progress; }

    public UUID getCreatorId() { return creatorId; }
    public void setCreatorId(UUID creatorId) { this.creatorId = creatorId; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        map.put("objectiveValue", objectiveValue);
        map.put("objectiveType", objectiveType);
        map.put("sport", sport);
        map.put("creatorId", creatorId);
        return map;
    }
}
