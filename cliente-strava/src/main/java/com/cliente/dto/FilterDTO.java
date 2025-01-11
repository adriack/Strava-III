package com.cliente.dto;

import java.time.LocalDate;

import com.cliente.entity.enumeration.SportType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;

@Schema(description = "Filter criteria for searching challenges or training sessions.")
public class FilterDTO {

    @Schema(description = "Start date for the filter. Challenges starting on or after this date will be included.", example = "2024-01-01")
    private LocalDate startDate = null;

    @Schema(description = "End date for the filter. Challenges ending on or before this date will be included.", example = "2024-12-31")
    private LocalDate endDate = null;

    @Schema(description = "Type of sport for the filter. Only challenges of this sport type will be included.", example = "CICLISMO")
    private SportType sport = null;

    @Schema(description = "Maximum number of challenges to retrieve.", example = "10")
    @Positive(message = "Limit must be a positive number.")
    private Integer limit = null;

    // Constructor vacío
    public FilterDTO() {
    }

    // Constructor con parámetros
    public FilterDTO(SportType sport, LocalDate startDate, LocalDate endDate) {
        this.sport = sport;
        this.startDate = startDate;
        this.endDate = endDate;
    }

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
    @Schema(description = "Ensures the end date is not before the start date.", hidden = true)
    public boolean isValidDateRange() {
        if (startDate != null && endDate != null) {
            return !endDate.isBefore(startDate);
        }
        return true;  // Si no se proporcionan fechas, la validación es válida
    }
}
