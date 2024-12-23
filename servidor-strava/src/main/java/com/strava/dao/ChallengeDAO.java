package com.strava.dao;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.strava.entity.Challenge;
import com.strava.entity.enumeration.SportType;

@Repository
public interface ChallengeDAO extends JpaRepository<Challenge, UUID> {

    @Query("SELECT c FROM Challenge c JOIN c.users u WHERE (:userId IS NULL OR u.id = :userId) " +
    "AND (:sport IS NULL OR c.sport = :sport) " +
    "AND (:startDate IS NULL OR c.startDate <= :endDate) " +
    "AND (:endDate IS NULL OR c.endDate >= :startDate) " +
    "ORDER BY c.startDate DESC")
    Page<Challenge> findFilteredChallenges(UUID userId, LocalDate startDate, LocalDate endDate, SportType sport, Pageable pageable);


}
