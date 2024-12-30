package com.strava.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Manejador para los errores de validación de objetos (por ejemplo, cuando se usa @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();

        // Extraer los errores de los campos del DTO
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });

        response.put("errors", fieldErrors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Manejador para la excepción de token inválido
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidTokenException(InvalidTokenException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // Manejador para MissingRequestHeaderException, cuando falta un encabezado requerido (por ejemplo, 'Authorization')
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<?> handleMissingRequestHeader(MissingRequestHeaderException ex) {
        Map<String, Object> response = new HashMap<>();
        
        // Crear un mapa con la clave "token" para el error
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("token", "Required request header 'Authorization' with user token is missing");
    
        // Agregar el mapa de errores a la clave "errors"
        response.put("errors", errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);  // Retornar 400 Bad Request
    }    

    // Manejador para HttpMessageNotReadableException cuando se pasa un valor incorrecto en un Enum
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Map<String, Map<String, String>> response = new HashMap<>();
        Map<String, String> errorDetails = new HashMap<>();
    
        // Detalle del error técnico
        errorDetails.put("json-parse", ex.getMessage());
    
        // Agregar el detalle a la clave "errors"
        response.put("errors", errorDetails);
    
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }    
    
}
