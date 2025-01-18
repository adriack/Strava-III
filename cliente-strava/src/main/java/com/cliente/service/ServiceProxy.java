package com.cliente.service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.stereotype.Service;

import com.cliente.dto.ClientErrorResponseDTO;
import com.cliente.dto.FilterDTO;
import com.cliente.dto.SuccessResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class ServiceProxy {
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String serverBaseUrl = "http://localhost:8080";
    private static final Logger logger = Logger.getLogger(ServiceProxy.class.getName());

    public ServiceProxy() {
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Registrar el módulo
    }

    private Object handleRequest(HttpUriRequestBase request) {
        try {
            if (request.getEntity() != null) {
                request.setHeader("Content-Type", "application/json");
            }

            return httpClient.execute(request, response -> {
                int statusCode = response.getCode();
                String responseBody = new String(response.getEntity().getContent().readAllBytes());

                if (statusCode >= 200 && statusCode < 300) {
                    logger.log(Level.INFO, "Successful response: {0} - {1}", new Object[] { statusCode, responseBody });
                    Map<String, Object> responseMap = objectMapper.readValue(responseBody,
                            new TypeReference<Map<String, Object>>() {
                            });
                    return new SuccessResponseDTO(responseMap);
                } else if (statusCode >= 400 && statusCode < 500) {
                    logger.log(Level.WARNING, "Client error response: {0} - {1}",
                            new Object[] { statusCode, responseBody });
                    Map<String, Object> errorMap = objectMapper.readValue(responseBody,
                            new TypeReference<Map<String, Object>>() {
                            });
                    return new ClientErrorResponseDTO(errorMap);
                }

                logger.log(Level.SEVERE, "Unexpected response: {0} - {1}", new Object[] { statusCode, responseBody });
                return null;
            });
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error executing request: {0}", e.getMessage());
            return null;
        }
    }

    private void setAuthHeader(HttpUriRequestBase request, String token) {
        if (token != null) {
            request.setHeader("Authorization", token);
        }
    }

    private StringEntity createJsonEntity(Object dto) throws IOException {
        return new StringEntity(objectMapper.writeValueAsString(dto), ContentType.APPLICATION_JSON);
    }

    public Object registerUser(Object registrationDTO) {
        try {
            HttpPost request = new HttpPost(serverBaseUrl + "/users/register");
            request.setEntity(createJsonEntity(registrationDTO));
            return handleRequest(request);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error creating request: {0}", e.getMessage());
            return null;
        }
    }

    public Object loginUser(Object loginDTO) {
        try {
            HttpPost request = new HttpPost(serverBaseUrl + "/users/login");
            request.setEntity(createJsonEntity(loginDTO));
            return handleRequest(request);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error creating request: {0}", e.getMessage());
            return null;
        }
    }

    public Object logoutUser(String token) {
        HttpPost request = new HttpPost(serverBaseUrl + "/users/logout");
        setAuthHeader(request, token);
        return handleRequest(request);
    }

    public Object getUserInfo(String token) {
        HttpGet request = new HttpGet(serverBaseUrl + "/users/info");
        setAuthHeader(request, token);
        return handleRequest(request);
    }

    public Object updateUserInfo(String token, Object userPhysicalInfoDTO) {
        try {
            HttpPatch request = new HttpPatch(serverBaseUrl + "/users/info");
            setAuthHeader(request, token);
            request.setEntity(createJsonEntity(userPhysicalInfoDTO));
            return handleRequest(request);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error creating request: {0}", e.getMessage());
            return null;
        }
    }

    public Object createSession(String token, Object sessionDTO) {
        try {
            HttpPost request = new HttpPost(serverBaseUrl + "/sessions");
            setAuthHeader(request, token);
            request.setEntity(createJsonEntity(sessionDTO));
            return handleRequest(request);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error creating request: {0}", e.getMessage());
            return null;
        }
    }

    public Object getUserSessions(String token, Object filterDTO) {
        FilterDTO filter = (FilterDTO) filterDTO;

        // Crear StringBuilder para construir la URL de manera eficiente
        StringBuilder urlBuilder = new StringBuilder(serverBaseUrl + "/sessions?");

        // Agregar parámetros a la URL solo si no son nulos ni vacíos
        if (filter.getStartDate() != null) {
            urlBuilder.append("startDate=").append(filter.getStartDate()).append("&");
        }
        if (filter.getEndDate() != null) {
            urlBuilder.append("endDate=").append(filter.getEndDate()).append("&");
        }
        if (filter.getSport() != null && !filter.getSport().toString().isEmpty()) {
            urlBuilder.append("sport=").append(filter.getSport().toString()).append("&");
        }
        if (filter.getLimit() != null) {
            urlBuilder.append("limit=").append(filter.getLimit()).append("&");
        }

        // Eliminar el último '&' si se añadió alguno
        String url = urlBuilder.toString();
        if (url.endsWith("&")) {
            url = url.substring(0, url.length() - 1);
        }

        // Crear la solicitud GET
        HttpGet request = new HttpGet(url);

        // Establecer el encabezado de autenticación
        setAuthHeader(request, token);

        // Manejar la solicitud y devolver la respuesta
        return handleRequest(request);
    }

    public Object deleteSession(String token, UUID sessionId) {
        HttpDelete request = new HttpDelete(serverBaseUrl + "/sessions/" + sessionId);
        setAuthHeader(request, token);
        return handleRequest(request);
    }

    public Object updateSession(String token, UUID sessionId, Object sessionDTO) {
        try {
            HttpPut request = new HttpPut(serverBaseUrl + "/sessions/" + sessionId);
            setAuthHeader(request, token);
            request.setEntity(createJsonEntity(sessionDTO));
            return handleRequest(request);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error creating request: {0}", e.getMessage());
            return null;
        }
    }

    public Object createChallenge(String token, Object challengeDTO) {
        try {
            HttpPost request = new HttpPost(serverBaseUrl + "/challenges");
            setAuthHeader(request, token);
            request.setEntity(createJsonEntity(challengeDTO));
            return handleRequest(request);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error creating request: {0}", e.getMessage());
            return null;
        }
    }

    public Object getActiveChallenges(Object filterDTO) {
        FilterDTO filter = (FilterDTO) filterDTO;

        // Crear StringBuilder para construir la URL de manera eficiente
        StringBuilder urlBuilder = new StringBuilder(serverBaseUrl + "/challenges?");

        // Agregar parámetros a la URL solo si no son nulos ni vacíos
        if (filter.getStartDate() != null) {
            urlBuilder.append("startDate=").append(filter.getStartDate()).append("&");
        }
        if (filter.getEndDate() != null) {
            urlBuilder.append("endDate=").append(filter.getEndDate()).append("&");
        }
        if (filter.getSport() != null && !filter.getSport().toString().isEmpty()) {
            urlBuilder.append("sport=").append(filter.getSport().toString()).append("&");
        }
        if (filter.getLimit() != null) {
            urlBuilder.append("limit=").append(filter.getLimit()).append("&");
        }

        // Eliminar el último '&' si se añadió alguno
        String url = urlBuilder.toString();
        if (url.endsWith("&")) {
            url = url.substring(0, url.length() - 1);
        }

        // Crear la solicitud GET
        HttpGet request = new HttpGet(url);

        // Manejar la solicitud y devolver la respuesta
        return handleRequest(request);
    }

    public Object acceptChallenge(String token, UUID challengeId) {
        HttpPost request = new HttpPost(serverBaseUrl + "/challenges/" + challengeId + "/accept");
        setAuthHeader(request, token);
        return handleRequest(request);
    }

    public Object getAcceptedChallenges(String token, boolean includeProgress) {
        HttpGet request = new HttpGet(serverBaseUrl + "/challenges/accepted?includeProgress=" + includeProgress);
        setAuthHeader(request, token);
        return handleRequest(request);
    }

    public Object getCreatedChallenges(UUID userId) {
        HttpGet request = new HttpGet(serverBaseUrl + "/challenges/created?userId=" + userId);
        return handleRequest(request);
    }

    public Object getChallenge(UUID challengeId) {
        HttpGet request = new HttpGet(serverBaseUrl + "/challenges/" + challengeId);
        return handleRequest(request);
    }

    public Object getChallengeParticipants(UUID challengeId) {
        HttpGet request = new HttpGet(serverBaseUrl + "/challenges/" + challengeId + "/participants");
        return handleRequest(request);
    }

    public Object isChallengeAcceptedByUser(UUID challengeId, UUID userId) {
        HttpGet request = new HttpGet(serverBaseUrl + "/challenges/" + challengeId + "/isAccepted?userId=" + userId);
        return handleRequest(request);
    }
}