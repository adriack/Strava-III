package com.cliente.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseWrapper<T> {
    private int statusCode; // Código HTTP
    private String message; // Mensaje descriptivo (opcional)
    private T data;         // Datos devueltos por el servicio

    public ResponseWrapper(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    // Getters
    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    // Setters
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    // Método para convertir el ResponseWrapper en ResponseEntity
    public ResponseEntity<Object> toResponseEntity() {
        HttpStatus httpStatus = HttpStatus.resolve(this.statusCode);
        if (httpStatus == null) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR; // Estado predeterminado
        }
    
        // Verificar si hay datos; si no, usa el mensaje como contenido de la respuesta
        String responseMessage = this.message != null ? this.message : "No content available";
    
        // Si hay datos, los añade junto con el mensaje
        Object responseData = this.data != null ? this.data : responseMessage;
    
        // Retorna la ResponseEntity con los datos y el mensaje
        return ResponseEntity.status(httpStatus).body(responseData);
    }
}
