package es.deusto.MetaAuthServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MetaAuthServer {
    private static final int PORT = 8082;
    private static final Map<String, String> users = new HashMap<>();
    private static final Logger logger = Logger.getLogger(MetaAuthServer.class.getName());

    // usuarios prueba
    static {
        users.put("ivan@meta.com", "ivan");
        users.put("javier@meta.com", "javier");
        users.put("alex@meta.com", "alex");
    }

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("MetaAuthServer is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error starting the server on port " + PORT, e);
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String request = in.readLine();
            logger.log(Level.INFO, "Received request: {0}", request);  // Log del comando recibido

            String[] parts = request.split(" ");
            String command = parts[0];

            switch (command) {
                case "REGISTER" -> {
                    if (parts.length < 3) {
                        out.println("REGISTER_FAIL");
                        logger.log(Level.WARNING, "Invalid REGISTER request: {0}", request);
                        break;
                    }
                    String email = parts[1];
                    String password = parts[2];
                    if (users.containsKey(email)) {
                        out.println("REGISTER_FAIL");
                        logger.log(Level.WARNING, "Registration failed. Email already exists: {0}", email);
                    } else {
                        users.put(email, password);
                        out.println("REGISTER_SUCCESS");
                        logger.log(Level.INFO, "User registered successfully: {0}", email);
                    }
                }

                case "VALIDATE_EMAIL" -> {
                    String email = parts[1];
                    out.println(users.containsKey(email) ? "EMAIL_VALID" : "EMAIL_INVALID");
                    logger.log(Level.INFO, "Email validation result for {0}: {1}", new Object[]{email, users.containsKey(email) ? "valid" : "invalid"});
                }

                case "VALIDATE_PASSWORD" -> {
                    String email = parts[1];
                    String password = parts[2];
                    if (users.containsKey(email) && users.get(email).equals(password)) {
                        out.println("PASSWORD_VALID");
                        logger.log(Level.INFO, "Password validation successful for {0}", email);
                    } else {
                        out.println("PASSWORD_INVALID");
                        logger.log(Level.WARNING, "Password validation failed for {0}", email);
                    }
                }

                default -> {
                    out.println("UNKNOWN_COMMAND");
                    logger.log(Level.WARNING, "Unknown command received: {0}", command);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error processing request", e);
        }
    }
}
