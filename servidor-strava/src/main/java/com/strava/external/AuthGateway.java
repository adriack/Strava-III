package com.strava.external;

import java.util.Optional;

public interface AuthGateway {
    public Optional<Boolean> validateEmail(String email);
    public Optional<Boolean> validatePassword(String email, String password);
}
