package com.adrian.almarsa.gestionfichajes.mvc.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// Captura excepciones de forma global en todos los controladores REST
@ControllerAdvice
public class GlobalExceptionHandler {

    // Maneja cualquier tipo de excepción no controlada específicamente
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {

        Map<String, Object> response = new HashMap<>();

        // Devuelve el mensaje de error en un formato JSON estructurado
        response.put("mensaje", "Ha ocurrido un error en el servidor");
        response.put("error", ex.getMessage());

        // Retorna un código 400 (Bad Request) o 500 según prefieras
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}