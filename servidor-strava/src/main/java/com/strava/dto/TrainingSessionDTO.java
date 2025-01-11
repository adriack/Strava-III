package com.strava.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strava.entity.TrainingSession;
import com.strava.entity.enumeration.SportType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

@Schema(description = "DTO representing a training session.")
public class TrainingSessionDTO {

    @Schema(description = "ID of the training session.", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true, hidden = true)
    private UUID id;

    @NotBlank(message = "Title is required.")
    @Schema(description = "Title of the training session.", example = "Morning Run")
    private String title;

    @NotNull(message = "Sport is required.")
    @Schema(description = "Type of sport for the session.", example = "RUNNING")
    private SportType sport;

    @NotNull(message = "Distance is required.")
    @Positive(message = "Distance must be greater than zero.")
    @Schema(description = "Distance covered during the session in kilometers.", example = "5.0")
    private Double distance;

    @NotNull(message = "Start date is required.")
    @PastOrPresent(message = "Start date cannot be in the future.")
    @Schema(description = "Date when the session started.", example = "2024-12-11")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "Start time is required.")
    @Schema(description = "Time when the session started.", example = "06:30")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull(message = "Duration is required.")
    @Positive(message = "Duration must be greater than zero.")
    @Schema(description = "Duration of the session in minutes.", example = "30.0")
    private Double duration;

    @JsonCreator
    public TrainingSessionDTO(
            @JsonProperty("title") String title,
            @JsonProperty("sport") SportType sport,
            @JsonProperty("distance") Double distance,
            @JsonProperty("startDate") LocalDate startDate,
            @JsonProperty("startTime") LocalTime startTime,
            @JsonProperty("duration") Double duration) {

        this.title = title;
        this.sport = sport;
        this.distance = distance;
        this.startDate = startDate;
        this.startTime = startTime;
        this.duration = duration;
    }

    public TrainingSessionDTO(TrainingSession session) {
        this.id = session.getId();
        this.title = session.getTitle();
        this.sport = session.getSport();
        this.distance = session.getDistance();
        this.startDate = session.getStartDate();
        this.startTime = session.getStartTime();
        this.duration = session.getDuration();
    }

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public SportType getSport() { return sport; }
    public void setSport(SportType sport) { this.sport = sport; }

    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public Double getDuration() { return duration; }
    public void setDuration(Double duration) { this.duration = duration; }

}
