package com.strava.dto;

import java.time.LocalDate;

import com.strava.entity.enumeration.SportType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;

@Schema(description = "Filter criteria for searching challenges.")
public class ChallengeFilterDTO {

    @Schema(description = "Start date for the filter. Challenges starting on or after this date will be included.", example = "2024-01-01")
    private LocalDate startDate = null;

    @Schema(description = "End date for the filter. Challenges ending on or before this date will be included.", example = "2024-12-31")
    private LocalDate endDate = null;

    @Schema(description = "Type of sport for the filter. Only challenges of this sport type will be included.", example = "CYCLING")
    private SportType sport = null;

    @Schema(description = "Maximum number of challenges to retrieve.", example = "10")
    private Integer limit = null;

    // Getters y Setters
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public SportType getSport() {
        return sport;
    }

    public void setSport(SportType sport) {
        this.sport = sport;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    // Validación personalizada para asegurarse de que endDate sea posterior o igual a startDate
    @AssertTrue(message = "End date must be greater than or equal to start date.")
    public boolean isValidDateRange() {
        if (startDate != null && endDate != null) {
            return !endDate.isBefore(startDate);
        }
        return true;  // Si no se proporcionan fechas, la validación es válida
    }
}
