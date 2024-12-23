package com.google.service;

import com.google.dao.GoogleUserRepository;
import com.google.entity.GoogleUser;
import com.google.dto.LoginDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.Collections;

@Service
public class GoogleAuthService {

    private final GoogleUserRepository userRepository;

    public GoogleAuthService(GoogleUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> validateEmailAndPassword(LoginDTO loginDTO) {
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();

        Optional<GoogleUser> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return ResponseEntity.ok(Collections.singletonMap("valid", true));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(Collections.singletonMap("error", "Invalid email or password"));
    }

    public ResponseEntity<?> registerUser(LoginDTO loginDTO) {
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();

        // Verifica si ya existe un usuario con el mismo correo
        Optional<GoogleUser> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(Collections.singletonMap("error", "Email already registered"));
        }

        // Si no existe, crear un nuevo usuario
        GoogleUser newUser = new GoogleUser();
        newUser.setEmail(email);
        newUser.setPassword(password);

        // Guardar el usuario en la base de datos
        userRepository.save(newUser);

        // Responder con un mensaje de éxito
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(Collections.singletonMap("message", "User registered successfully"));
    }

    public boolean verifyEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
