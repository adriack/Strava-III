
package com.cliente.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cliente.dto.ChallengeDTO;
import com.cliente.dto.ResponseWrapper;

@Service
public class ChallengeServiceProxy {

    private final RestTemplate restTemplate;

    public ChallengeServiceProxy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseWrapper<ChallengeDTO[]> getActiveChallenges(String startDate, String endDate) {
        // ...existing code...
    }

    public ResponseWrapper<ChallengeDTO[]> getAcceptedChallenges() {
        // ...existing code...
    }
}