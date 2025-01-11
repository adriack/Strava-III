package com.cliente.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.cliente.entity.enumeration.SportType;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

@Schema(description = "DTO representing a training session.")
public class TrainingSessionDTO {

    @Schema(description = "ID of the training session.", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true, hidden = true)
    private UUID id;

    @NotBlank(message = "El título es obligatorio.")
    @Schema(description = "Title of the training session.", example = "Morning Run")
    private String title;

    @NotNull(message = "El deporte es obligatorio.")
    @Schema(description = "Type of sport for the session.", example = "RUNNING")
    private SportType sport;

    @NotNull(message = "La distancia es obligatoria.")
    @Positive(message = "La distancia debe ser mayor que cero.")
    @Schema(description = "Distance covered during the session in kilometers.", example = "5.0")
    private Double distance;

    @NotNull(message = "La fecha de inicio es obligatoria.")
    @PastOrPresent(message = "La fecha de inicio no puede estar en el futuro.")
    @Schema(description = "Date when the session started.", example = "2024-12-11")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "La hora de inicio es obligatoria.")
    @Schema(description = "Time when the session started.", example = "06:30")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull(message = "La duración es obligatoria.")
    @Positive(message = "La duración debe ser mayor que cero.")
    @Schema(description = "Duration of the session in minutes.", example = "30.0")
    private Double duration;

    public TrainingSessionDTO() {
        // Constructor vacío
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
