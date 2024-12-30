package com.strava.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.strava.entity.User;
import com.strava.entity.enumeration.AuthProvider;

public class UserDTO {
    private UUID id;
    private String email;
    private String name;
    private LocalDate dateOfBirth;
    private Double weight;
    private Double height;
    private Integer maxHeartRate;
    private Integer restingHeartRate;
    private AuthProvider authProvider;

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.dateOfBirth = user.getDateOfBirth();
        this.weight = user.getWeight();
        this.height = user.getHeight();
        this.maxHeartRate = user.getMaxHeartRate();
        this.restingHeartRate = user.getRestingHeartRate();
        this.authProvider = user.getAuthProvider();
    }

    // Getters y Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Integer getMaxHeartRate() {
        return maxHeartRate;
    }

    public void setMaxHeartRate(Integer maxHeartRate) {
        this.maxHeartRate = maxHeartRate;
    }

    public Integer getRestingHeartRate() {
        return restingHeartRate;
    }

    public void setRestingHeartRate(Integer restingHeartRate) {
        this.restingHeartRate = restingHeartRate;
    }

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }
}
