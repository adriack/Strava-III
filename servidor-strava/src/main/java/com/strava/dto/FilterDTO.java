package com.strava.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strava.entity.enumeration.SportType;

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

    @JsonCreator
    public FilterDTO(
        @JsonProperty("startDate") LocalDate startDate,
        @JsonProperty("endDate") LocalDate endDate,
        @JsonProperty("sport") SportType sport,
        @JsonProperty("limit") Integer limit) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.sport = sport;
        this.limit = limit;
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

}
