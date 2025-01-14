package com.cliente.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.cliente.entity.enumeration.ObjectiveType;
import com.cliente.entity.enumeration.SportType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
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
}
