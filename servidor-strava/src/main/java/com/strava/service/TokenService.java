package com.strava.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.strava.dao.TokenDAO;
import com.strava.dto.TokenDTO;
import com.strava.entity.User;
import com.strava.exception.InvalidTokenException;

@Service
public class TokenService {

    @Autowired
    private TokenDAO tokenDAO;

    // Función para generar un token único basado en el timestamp actual
    public String generateToken() {
        return String.valueOf(System.currentTimeMillis());
    }

    // Validar el token y obtener el usuario asociado
    public User getUserFromToken(TokenDTO tokenDTO) {
        String token = tokenDTO.getToken();
    
        Optional<User> user = tokenDAO.findUserByToken(token);
        if (user.isEmpty()) {
            throw new InvalidTokenException("Invalid or revoked token.");
        }
    
        return user.get();
    }
}
