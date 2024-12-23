package com.strava.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

@Component
public class MetaAuthGateway implements AuthGateway {
    private static final String HOST = "localhost";
    private static final int PORT = 8082;
    private static final Logger logger = Logger.getLogger(MetaAuthGateway.class.getName());

    @Override
    public Optional<Boolean> validateEmail(String email) {
        try {
            String response = sendRequest("VALIDATE_EMAIL " + email);
            return Optional.of(response.equals("EMAIL_VALID"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error communicating with MetaAuthServer while validating email", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Boolean> validatePassword(String email, String password) {
        try {
            String response = sendRequest("VALIDATE_PASSWORD " + email + " " + password);
            return Optional.of(response.equals("PASSWORD_VALID"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error communicating with MetaAuthServer while validating password", e);
            return Optional.empty();
        }
    }

    private String sendRequest(String request) throws IOException {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println(request);
            String response = in.readLine();
            if (response == null) {
                logger.warning("Received null response from server");
            } else {
                logger.info("Received response: " + response);
            }
            return response != null ? response : "ERROR";
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error during request to MetaAuthServer: " + request, e);
            throw e;
        }
    }
}
