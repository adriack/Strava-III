package com.cliente.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.cliente.dto.LoginDTO;
import com.cliente.dto.TokenDTO;
import com.cliente.dto.UserDTO;

@Service
public class ServiceProxy {

    private final RestTemplate restTemplate;
    private final String serverBaseUrl = "http://localhost:8080/users"; // Base URL del servidor

    public ServiceProxy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Registrar usuario
    public ResponseEntity<?> registerUser(UserDTO user) {
        String url = serverBaseUrl + "/register";
        try {
            return restTemplate.postForEntity(url, user, Object.class);
        } catch (HttpClientErrorException e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("status", e.getStatusCode().toString());
            errorMap.put("body", e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(errorMap);
        }
    }

    // Login de usuario
    public ResponseEntity<?> loginUser(LoginDTO login) {
        String url = serverBaseUrl + "/login";
        try {
            return restTemplate.postForEntity(url, login, Object.class);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    // Logout de usuario
    public ResponseEntity<?> logoutUser(TokenDTO token) {
        String url = serverBaseUrl + "/logout";
        return restTemplate.postForEntity(url, token, Object.class);
    }

    // Obtener información del usuario
    public ResponseEntity<?> getUserInfo(TokenDTO token) {
        String url = serverBaseUrl + "/info";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.getToken()); // Si se usa un token de autenticación
        HttpEntity<TokenDTO> entity = new HttpEntity<>(token, headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
    }
}
