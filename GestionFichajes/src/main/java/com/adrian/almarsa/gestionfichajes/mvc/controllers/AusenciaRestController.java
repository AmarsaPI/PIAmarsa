package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import java.time.LocalDate;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Ausencia;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IAusenciaService;

/**
 * Controlador REST encargado de gestionar las ausencias.
 * Permite crear, consultar y eliminar ausencias de empleados.
 */
@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api")
public class AusenciaRestController {

    @Autowired
    private IAusenciaService ausenciaService;

    /**
     * Registra una nueva ausencia.
     * 
     * @param ausencia datos de la ausencia
     * @return respuesta con la ausencia creada
     */
    
    // Registrar una nueva ausencia
    // POST: /api/ausencias
    @PostMapping("/ausencias")
    public ResponseEntity<?> create(@RequestBody Ausencia ausencia) {
        
        Map<String, Object> response = new HashMap<>();
        Ausencia ausenciaNew;

        try {
            ausenciaNew = ausenciaService.registrarAusencia(ausencia);

        } catch (DataAccessException e) {

            response.put("mensaje", "Error al registrar la ausencia en la base de datos");

            response.put("error", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "Ausencia registrada correctamente");

        response.put("ausencia", ausenciaNew);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Obtiene todas las ausencias de un empleado.
     * 
     * @param empleadoId id del empleado
     * @return lista de ausencias del empleado
     */
    
    // Obtener todas las ausencias de un empleado
    // Útil para mostrar el calendario del usuario
    // GET: /api/ausencias/empleado/{empleadoId}
    @GetMapping("/ausencias/empleado/{empleadoId}")
    public ResponseEntity<?> listarAusenciasPorEmpleado(@PathVariable Long empleadoId) {
        
        Map<String, Object> response = new HashMap<>();
        List<Ausencia> ausencias = null;

        try {

            Empleado empleado = new Empleado();

            empleado.setId(empleadoId);

            ausencias = ausenciaService.obtenerAusenciasPorEmpleado(empleado);

        } catch (DataAccessException e) {

            response.put("mensaje", "Error al consultar las ausencias");

            response.put("error", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Comprueba si el empleado tiene ausencias
        if (ausencias == null || ausencias.isEmpty()) {

            response.put("mensaje", "El empleado no tiene ausencias registradas");

            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(ausencias, HttpStatus.OK);
    }

    /**
     * Comprueba si un empleado está ausente en una fecha concreta.
     * 
     * @param empleadoId id del empleado
     * @param fecha fecha que se quiere comprobar
     * @return estado de ausencia del empleado
     */
    
    // Comprobar si un empleado está ausente en una fecha concreta
    // GET: /api/ausencias/verificar-estado?empleadoId=1&fecha=2026-05-19
    @GetMapping("/ausencias/verificar-estado")
    public ResponseEntity<?> verificarAusencia(

            @RequestParam Long empleadoId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha) {

        Map<String, Object> response = new HashMap<>();

        boolean estaAusente = false;

        try {

            Empleado empleado = new Empleado();

            empleado.setId(empleadoId);

            // Comprueba si el empleado tiene ausencia ese día
            estaAusente =
                    ausenciaService.esEmpleadoAusente(empleado, fecha);

        } catch (DataAccessException e) {

            response.put("mensaje", "Error al comprobar la ausencia");

            response.put("error", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("empleadoId", empleadoId);

        response.put("fecha", fecha);

        response.put("ausente", estaAusente);

        response.put(
                "mensaje",
                estaAusente
                        ? "El empleado está ausente este día"
                        : "El empleado no tiene ausencias este día"
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Elimina una ausencia registrada.
     * 
     * @param id id de la ausencia
     * @return mensaje de confirmación
     */
    
    // Eliminar una ausencia
    // Por ejemplo si se cancelan vacaciones o termina una baja antes de tiempo
    // DELETE: /api/ausencias/{id}
    @DeleteMapping("/ausencias/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();

        try {

            ausenciaService.eliminarAusencia(id);

        } catch (DataAccessException e) {

            response.put("mensaje", "Error al eliminar la ausencia");

            response.put("error", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "Ausencia eliminada correctamente");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}