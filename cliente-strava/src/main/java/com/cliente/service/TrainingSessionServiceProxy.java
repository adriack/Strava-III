
package com.cliente.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cliente.dto.ResponseWrapper;
import com.cliente.dto.TrainingSessionDTO;

@Service
public class TrainingSessionServiceProxy {

    private final RestTemplate restTemplate;

    public TrainingSessionServiceProxy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseWrapper<TrainingSessionDTO[]> getUserSessions() {
        // ...existing code...
    }
}