package com.strava.dao;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.strava.entity.User;
import com.strava.entity.UserToken;

@Repository
public interface TokenDAO extends JpaRepository<UserToken, UUID> {

    // Busca un token específico, solo si no está revocado
    @Query("SELECT ut FROM UserToken ut WHERE ut.token = :token AND ut.revoked = false")
    Optional<UserToken> findByToken(String token);

    // Busca el usuario asociado a un token no revocado
    @Query("SELECT ut.user FROM UserToken ut WHERE ut.token = :token AND ut.revoked = false")
    Optional<User> findUserByToken(String token);

}

