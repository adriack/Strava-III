package com.cliente.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.cliente.dto.LoginDTO;
import com.cliente.dto.ResponseWrapper;
import com.cliente.dto.TokenDTO;
import com.cliente.dto.UserDTO;

@Service
public class UserServiceProxy {

    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8080/users";

    public UserServiceProxy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseWrapper<UserDTO> getUserInfo() {
        // Implementación pendiente
        // ...existing code...
    }

    public ResponseWrapper<TokenDTO> login(String email, String password) {
        LoginDTO loginDTO = new LoginDTO(email, password);
        HttpEntity<LoginDTO> request = new HttpEntity<>(loginDTO);
        ResponseEntity<TokenDTO> response = restTemplate.exchange(
            BASE_URL + "/login",
            HttpMethod.POST,
            request,
            TokenDTO.class
        );
        return new ResponseWrapper<>(response.getBody(), response.getStatusCode());
    }

    public ResponseWrapper<Void> logout(String token) {
        TokenDTO tokenDTO = new TokenDTO(token);
        HttpEntity<TokenDTO> request = new HttpEntity<>(tokenDTO);
        ResponseEntity<Void> response = restTemplate.exchange(
            BASE_URL + "/logout",
            HttpMethod.POST,
            request,
            Void.class
        );
        return new ResponseWrapper<>(response.getBody(), response.getStatusCode());
    }

    public ResponseWrapper<Void> register(String email, String password) {
        UserDTO userDTO = new UserDTO(email, password);
        HttpEntity<UserDTO> request = new HttpEntity<>(userDTO);
        ResponseEntity<Void> response = restTemplate.exchange(
            BASE_URL + "/register",
            HttpMethod.POST,
            request,
            Void.class
        );
        return new ResponseWrapper<>(response.getBody(), response.getStatusCode());
    }
}