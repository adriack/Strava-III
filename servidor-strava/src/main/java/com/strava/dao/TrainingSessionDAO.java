package com.strava.dao;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.strava.entity.TrainingSession;

@Repository
public interface TrainingSessionDAO extends JpaRepository<TrainingSession, UUID> {

    @Query("SELECT t FROM TrainingSession t WHERE t.user.id = :userId AND (t.startDate >= :startDate OR :startDate IS NULL) AND (t.startDate <= :endDate OR :endDate IS NULL) ORDER BY t.startDate DESC")
    Page<TrainingSession> findFilteredSessions(UUID userId, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
