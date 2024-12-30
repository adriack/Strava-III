package com.strava.dto;

import java.util.UUID;

public class UserProgressDTO {
    private UUID userId;
    private String userName;
    private double progress;

    public UserProgressDTO(UUID userId, String userName, double progress) {
        this.userId = userId;
        this.userName = userName;
        this.progress = progress;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }
}
