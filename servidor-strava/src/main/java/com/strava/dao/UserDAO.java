package com.strava.dao;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.strava.entity.User;

@Repository  // Marca esta clase como un componente DAO de acceso a datos.
public interface UserDAO extends JpaRepository<User, UUID> {

    // Busca un usuario por su correo electrónico
    Optional<User> findByEmail(String email);

}
