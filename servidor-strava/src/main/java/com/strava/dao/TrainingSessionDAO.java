package com.strava.dao;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.strava.entity.TrainingSession;
import com.strava.entity.enumeration.SportType;

@Repository
public interface TrainingSessionDAO extends JpaRepository<TrainingSession, UUID> {

    @Query("SELECT s FROM TrainingSession s JOIN s.user u WHERE (:userId IS NULL OR u.id = :userId) " +
    "AND (:sport IS NULL OR s.sport = :sport) " +
    "AND (:startDate IS NULL OR s.startDate >= :startDate) " +
    "AND (:endDate IS NULL OR s.startDate <= :endDate) " +
    "ORDER BY s.startDate DESC")
    Page<TrainingSession> findFilteredSessions(UUID userId, LocalDate startDate, LocalDate endDate, SportType sport, Pageable pageable);

}
