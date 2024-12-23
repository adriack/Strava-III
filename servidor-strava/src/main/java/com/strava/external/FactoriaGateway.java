package com.strava.external;

import org.springframework.stereotype.Component;

import com.strava.entity.enumeration.AuthProvider;

@Component
public class FactoriaGateway {

    public AuthGateway createGateway(AuthProvider provider) {
        return switch (provider) {
            case META -> new MetaAuthGateway();
            case GOOGLE -> new GoogleAuthGateway();
        };
    }
}
