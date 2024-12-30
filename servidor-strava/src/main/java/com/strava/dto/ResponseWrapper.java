package com.strava.dto;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL) // Excluye campos nulos en la respuesta JSON
public class ResponseWrapper {
    private int statusCode; // Código HTTP
    private Map<String, Object> data; // Datos devueltos por el servicio, siempre como un Map

    // Constructor principal
    public ResponseWrapper(int statusCode, Map<String, Object> data) {
        this.statusCode = statusCode;
        this.data = data;
    }

    // Constructor alternativo
    public ResponseWrapper(int statusCode, String key, Object value) {
        this.statusCode = statusCode;
        this.data = new HashMap<>();
        this.data.put(key, value);
    }

    // Getters
    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, Object> getData() {
        return data;
    }

    // Setters
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    // Método para convertir el ResponseWrapper en ResponseEntity
    public ResponseEntity<Object> toResponseEntity() {
        HttpStatus httpStatus = HttpStatus.resolve(this.statusCode);
        if (httpStatus == null) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR; // Estado predeterminado
        }

        // Retorna la ResponseEntity con los datos serializados como JSON
        return ResponseEntity.status(httpStatus).body(this.getData());
    }

}
