package com.strava.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;

@Schema(description = "DTO for filtering user sessions.")
public class SessionFilterDTO {

    @Schema(description = "The start date for filtering sessions.", example = "2023-01-01")
    private LocalDate startDate = null;

    @Schema(description = "The end date for filtering sessions.", example = "2023-12-31")
    private LocalDate endDate = null;

    @Schema(description = "Maximum number of results to return.", example = "10")
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

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    // Validación personalizada para asegurarse de que endDate sea posterior o igual a startDate
    @AssertTrue(message = "End date must be greater than or equal to start date.")
    @Schema(description = "Ensures the end date is not before the start date.", hidden = true)
    public boolean isValidDateRange() {
        if (startDate != null && endDate != null) {
            return !endDate.isBefore(startDate);
        }
        return true; // Si no se proporcionan fechas, la validación es válida
    }
}
