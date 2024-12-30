package com.cliente.entity;

import com.cliente.dto.TrainingSessionDTO;
import com.cliente.entity.enumeration.SportType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity  // Marca esta clase como una entidad persistente
@Table(name = "sessions")
public class TrainingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)  // Generación automática del ID
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)  // Para almacenar los valores de SportType como cadenas
    private SportType sport;

    @Column(nullable = false)
    private Double distance;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private Double duration;

    // Relación ManyToOne: Una sesión pertenece a un solo usuario
    @ManyToOne(fetch = FetchType.LAZY)  // Usamos LAZY loading para evitar cargar los usuarios innecesariamente
    @JoinColumn(name = "user_id", nullable = false)  // La columna que guarda la relación con el usuario
    private User user;

    // Constructor vacío para JPA
    public TrainingSession() {}

    // Constructor que convierte un DTO en una entidad TrainingSession
    public TrainingSession(TrainingSessionDTO sessionDTO) {
        this.title = sessionDTO.getTitle();
        this.sport = sessionDTO.getSport();
        this.distance = sessionDTO.getDistance();
        this.startDate = sessionDTO.getStartDate();
        this.startTime = sessionDTO.getStartTime();
        this.duration = sessionDTO.getDuration();
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

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
