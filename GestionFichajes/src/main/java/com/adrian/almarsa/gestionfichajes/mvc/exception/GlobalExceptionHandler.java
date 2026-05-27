package com.adrian.almarsa.gestionfichajes.mvc.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Manejador global de excepciones para toda la aplicación.
 * 
 * Esta clase captura cualquier error no controlado que ocurra en los
 * controladores y devuelve una respuesta JSON uniforme, evitando que
 * el usuario reciba trazas internas o mensajes poco claros.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	/**
     * Captura cualquier excepción no gestionada de forma específica.
     * Devuelve un mensaje genérico y el detalle del error para facilitar
     * el diagnóstico sin exponer información sensible.
     *
     * @param ex excepción lanzada durante la ejecución
     * @return respuesta JSON con el mensaje de error y código HTTP 400
     */
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