package com.strava.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.strava.dto.UserDTO;
import com.strava.entity.enumeration.AuthProvider;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users") // La tabla en la base de datos se llamará 'users'
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = true)
    private Double weight;

    @Column(nullable = true)
    private Double height;

    @Column(nullable = true)
    private Integer maxHeartRate;

    @Column(nullable = true)
    private Integer restingHeartRate;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    // Relación Uno a Varios con TrainingSession
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrainingSession> sessions = new ArrayList<>();

    // Relación Varios a Varios con Challenge
    @ManyToMany
    @JoinTable(
        name = "user_challenges",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "challenge_id")
    )
    private List<Challenge> challenges = new ArrayList<>();

    // Relación Uno a Varios con Challenge (retos creados por el usuario)
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Challenge> createdChallenges = new ArrayList<>();

    // Relación Uno a Varios con UserToken
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserToken> tokens = new ArrayList<>();

    public User() {
        // Constructor vacío requerido por JPA
    }

    public User(UserDTO userDTO) {
        this.email = userDTO.getEmail();
        this.name = userDTO.getName();
        this.dateOfBirth = userDTO.getDateOfBirth();
        this.weight = userDTO.getWeight();
        this.height = userDTO.getHeight();
        this.maxHeartRate = userDTO.getMaxHeartRate();
        this.restingHeartRate = userDTO.getRestingHeartRate();
        this.authProvider = userDTO.getAuthProvider();
    }

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public Integer getMaxHeartRate() { return maxHeartRate; }
    public void setMaxHeartRate(Integer maxHeartRate) { this.maxHeartRate = maxHeartRate; }

    public Integer getRestingHeartRate() { return restingHeartRate; }
    public void setRestingHeartRate(Integer restingHeartRate) { this.restingHeartRate = restingHeartRate; }

    public AuthProvider getAuthProvider() { return authProvider; }
    public void setAuthProvider(AuthProvider authProvider) { this.authProvider = authProvider; }

    // Métodos de conveniencia para sesiones
    public List<TrainingSession> getSessions() { return sessions; }
    public void setSessions(List<TrainingSession> sessions) { this.sessions = sessions; }

    public void addSession(TrainingSession session) {
        if (session != null && !sessions.contains(session)) {
            sessions.add(session);
            session.setUser(this);
        }
    }

    public void removeSession(TrainingSession session) {
        if (session != null && sessions.contains(session)) {
            sessions.remove(session);
            session.setUser(null);
        }
    }

    // Métodos de conveniencia para retos
    public List<Challenge> getChallenges() { return challenges; }
    public void setChallenges(List<Challenge> challenges) { this.challenges = challenges; }

    public void addChallenge(Challenge challenge) {
        if (challenge != null && !challenges.contains(challenge)) {
            challenges.add(challenge);
        }
    }

    public void removeChallenge(Challenge challenge) {
        if (challenge != null && challenges.contains(challenge)) {
            challenges.remove(challenge);
        }
    }

    // Métodos de conveniencia para retos creados
    public List<Challenge> getCreatedChallenges() { return createdChallenges; }
    public void setCreatedChallenges(List<Challenge> createdChallenges) { this.createdChallenges = createdChallenges; }

    public void addCreatedChallenge(Challenge challenge) {
        if (challenge != null && !createdChallenges.contains(challenge)) {
            createdChallenges.add(challenge);
            challenge.setCreator(this);
        }
    }

    public void removeCreatedChallenge(Challenge challenge) {
        if (challenge != null && createdChallenges.contains(challenge)) {
            createdChallenges.remove(challenge);
            challenge.setCreator(null);
        }
    }

    // Métodos de conveniencia para tokens
    public List<UserToken> getTokens() { return tokens; }
    public void setTokens(List<UserToken> tokens) { this.tokens = tokens; }

    public void addToken(UserToken token) {
        if (token != null && !tokens.contains(token)) {
            tokens.add(token);
            token.setUser(this);
        }
    }

    public void removeToken(UserToken token) {
        if (token != null && tokens.contains(token)) {
            tokens.remove(token);
            token.setUser(null);
        }
    }

    public void revokeToken(UserToken token) {
        if (token != null && tokens.contains(token)) {
            token.setRevoked(true);
        }
    }
}
