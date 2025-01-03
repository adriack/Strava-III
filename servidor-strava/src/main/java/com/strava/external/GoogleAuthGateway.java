package com.strava.external;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GoogleAuthGateway implements AuthGateway {

    private static final String GOOGLE_API_BASE_URL = "http://localhost:8081/api/google";
    private final HttpClient httpClient;
    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthGateway.class);

    public GoogleAuthGateway() {
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public Optional<Boolean> validateEmail(String email) {
        String url = GOOGLE_API_BASE_URL + "/verify-email?email=" + email;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                // Parse the response to check if the email is registered
                return Optional.of(responseBody.contains("\"registered\":true"));
            } else {
                logger.warn("Failed to validate email {}. HTTP Status: {}", email, response.statusCode());
                return Optional.of(false);
            }
        } catch (IOException ex) {
            logger.error("IO error occurred while validating email: {}", email, ex);
            return Optional.empty();
        } catch (InterruptedException ex) {
            logger.error("Request interrupted while validating email: {}", email, ex);
            Thread.currentThread().interrupt(); // Restore the interrupted status
            return Optional.empty();
        }
    }

    @Override
    public Optional<Boolean> validatePassword(String email, String password) {
        String url = GOOGLE_API_BASE_URL + "/validate";

        try {
            String requestBody = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                // Parse the response to check if credentials are valid
                return Optional.of(responseBody.contains("\"valid\":true"));
            } else {
                logger.info("Invalid password for email {}. HTTP Status: {}", email, response.statusCode());
                return Optional.of(false);
            }
        } catch (IOException ex) {
            logger.error("IO error occurred while validating password for email: {}", email, ex);
            return Optional.empty();
        } catch (InterruptedException ex) {
            logger.error("Request interrupted while validating password for email: {}", email, ex);
            Thread.currentThread().interrupt(); // Restore the interrupted status
            return Optional.empty();
        }
    }
    
}
