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

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api")
public class AusenciaRestController {

    @Autowired
    private IAusenciaService ausenciaService;

    // 1. REGISTRAR UNA NUEVA AUSENCIA (Vacaciones, Baja Médica, Asuntos Propios...)
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

        response.put("mensaje", "Ausencia registrada con éxito");
        response.put("ausencia", ausenciaNew);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. OBTENER TODAS LAS AUSENCIAS DE UN EMPLEADO (Útil para pintar el calendario de un usuario)
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
            response.put("mensaje", "Error al consultar las ausencias en la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (ausencias == null || ausencias.isEmpty()) {
            response.put("mensaje", "El empleado con ID: " + empleadoId + " no tiene ausencias o vacaciones registradas.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(ausencias, HttpStatus.OK);
    }

    // 3. COMPROBAR SI UN EMPLEADO ESTÁ AUSENTE HOY O EN UNA FECHA CONCRETA
    // GET: /api/ausencias/verificar-estado?empleadoId=1&fecha=2026-05-19
    @GetMapping("/ausencias/verificar-estado")
    public ResponseEntity<?> verificarAusencia(
            @RequestParam Long empleadoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        Map<String, Object> response = new HashMap<>();
        boolean estaAusente = false;

        try {
            Empleado empleado = new Empleado();
            empleado.setId(empleadoId);
            estaAusente = ausenciaService.esEmpleadoAusente(empleado, fecha);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al verificar el estado de ausencia en la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("empleadoId", empleadoId);
        response.put("fecha", fecha);
        response.put("ausente", estaAusente);
        response.put("mensaje", estaAusente ? "El empleado está de baja o vacaciones este día." : "El empleado no tiene ausencias registradas este día.");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 4. ELIMINAR UNA AUSENCIA (Por ejemplo, si se cancelan unas vacaciones o se da un alta médica anticipada)
    // DELETE: /api/ausencias/{id}
    @DeleteMapping("/ausencias/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();

        try {
            ausenciaService.eliminarAusencia(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar la ausencia de la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "Ausencia eliminada con éxito");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}