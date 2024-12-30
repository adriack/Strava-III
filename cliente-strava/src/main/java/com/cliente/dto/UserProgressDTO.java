package com.cliente.dto;

public class UserProgressDTO {
    private String userName;
    private double progress;

    public UserProgressDTO(String userName, double progress) {
        this.userName = userName;
        this.progress = progress;
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
