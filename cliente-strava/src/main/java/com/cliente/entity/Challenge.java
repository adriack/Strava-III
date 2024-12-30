package com.cliente.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.cliente.dto.ChallengeDTO;
import com.cliente.entity.enumeration.ObjectiveType;
import com.cliente.entity.enumeration.SportType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "challenges")  // La tabla en la base de datos se llamará 'challenges'
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Double objectiveValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ObjectiveType objectiveType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SportType sport;

    @ManyToMany(mappedBy = "challenges")  // Relación bidireccional con User
    private List<User> users = new ArrayList<>();  // Listado de usuarios que participan en el reto

    // Relación Muchos a Uno con User (usuario que ha creado el reto)
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    // Constructor vacío para JPA
    public Challenge() {}

    // Constructor utilizando ChallengeDTO
    public Challenge(ChallengeDTO challengeDTO) {
        this.name = challengeDTO.getName();
        this.startDate = challengeDTO.getStartDate();
        this.endDate = challengeDTO.getEndDate();
        this.objectiveValue = challengeDTO.getObjectiveValue();
        this.objectiveType = challengeDTO.getObjectiveType();
        this.sport = challengeDTO.getSport();
    }

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Double getObjectiveValue() { return objectiveValue; }
    public void setObjectiveValue(Double objectiveValue) { this.objectiveValue = objectiveValue; }

    public ObjectiveType getObjectiveType() { return objectiveType; }
    public void setObjectiveType(ObjectiveType objectiveType) { this.objectiveType = objectiveType; }

    public SportType getSport() { return sport; }
    public void setSport(SportType sport) { this.sport = sport; }

    // Métodos para los usuarios
    public List<User> getUsers() { return new ArrayList<>(users); }
    public void setUsers(List<User> users) { this.users = users; }

    public void addUser(User user) {
        if (user != null && !users.contains(user)) {
            users.add(user);
            user.addChallenge(this);  // Aseguramos que la relación sea bidireccional
        }
    }

    public void removeUser(User user) {
        if (user != null && users.contains(user)) {
            users.remove(user);  // Eliminar el usuario de la lista de usuarios del desafío
            user.removeChallenge(this);  // Aseguramos que la relación sea eliminada en el otro lado también
        }
    }

    // Métodos para el creador del reto
    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }

}
