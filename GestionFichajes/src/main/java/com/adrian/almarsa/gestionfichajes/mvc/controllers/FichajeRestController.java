package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Fichaje;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IFichajeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"*"})
public class FichajeRestController {

    @Autowired
    private IFichajeService fichajeService;

    // --- MÉTODOS ORIGINALES (MANTENIDOS) ---

    @GetMapping("/fichajes")
    public List<Fichaje> index() {
        return fichajeService.findAll();
    }
    
    @GetMapping("/fichajes/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Fichaje fichaje = fichajeService.findById(id);
            if (fichaje == null) {
                response.put("mensaje", "El fichaje ID: " + id + " no existe");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(fichaje, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al consultar el fichaje");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/fichajes")
    public ResponseEntity<?> create(@Valid @RequestBody Fichaje fichaje, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            Fichaje fichajeNew = fichajeService.registrarEntrada(fichaje);
            response.put("mensaje", "Fichaje registrado con éxito");
            response.put("fichaje", fichajeNew);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            response.put("mensaje", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PutMapping("/fichajes/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Fichaje fichaje, BindingResult result, @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            Fichaje fichajeActual = fichajeService.findById(id);
            if (fichajeActual == null) {
                response.put("mensaje", "Error: no se pudo editar, el fichaje ID: " + id + " no existe");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            // Lógica original: setFechaEntrada comentada
            fichajeActual.setFechaSalida(LocalDateTime.now());
            if(fichaje.getEmpleado() != null) fichajeActual.setEmpleado(fichaje.getEmpleado());

            Fichaje fichajeUpdated = fichajeService.save(fichajeActual);
            response.put("mensaje", "Fichaje actualizado con éxito");
            response.put("fichaje", fichajeUpdated);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar el fichaje");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/fichajes/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (fichajeService.findById(id) == null) {
                response.put("mensaje", "El fichaje ID: " + id + " no existe");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            fichajeService.delete(id);
            response.put("mensaje", "Fichaje eliminado con éxito");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar el fichaje");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/fichajes/empleado/{empleadoId}")
    public ResponseEntity<?> fichajesPorEmpleado(@PathVariable Long empleadoId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Fichaje> fichajes = fichajeService.findByEmpleado(empleadoId);
            if (fichajes.isEmpty()) {
                response.put("mensaje", "No hay fichajes para el empleado ID: " + empleadoId);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(fichajes, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al consultar fichajes");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/fichajes/empleado/{empleadoId}/SemanaActual")
    public ResponseEntity<?> fichajesPorEmpleadoSemanaActual(@PathVariable Long empleadoId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Fichaje> fichajes = fichajeService.findByEmpleadoSemanaActual(empleadoId);
            if (fichajes.isEmpty()) {
                response.put("mensaje", "No hay fichajes para el empleado ID: " + empleadoId);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(fichajes, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al consultar fichajes");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/fichajes/activo/{empleadoId}")
    public ResponseEntity<?> obtenerFichajeActivo(@PathVariable Long empleadoId) {
        Fichaje activo = fichajeService.findUltimoSinCerrar(empleadoId);
        if (activo == null) {
            return ResponseEntity.ok(Map.of("enJornada", false, "mensaje", "No hay jornada activa"));
        }
        return ResponseEntity.ok(activo);
    }

    // --- MÉTODOS NUEVOS (MODIFICACIONES DE LA SEGUNDA CLASE) ---

    @PutMapping("/fichajes/update/full/{id}")
    public ResponseEntity<?> updateFull(@Valid @RequestBody Fichaje fichaje, BindingResult result, @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Fichaje fichajeActual = fichajeService.findById(id);
            fichajeActual.setFechaEntrada(fichaje.getFechaEntrada()); 
            fichajeActual.setFechaSalida(LocalDateTime.now());
            if(fichaje.getEmpleado() != null) fichajeActual.setEmpleado(fichaje.getEmpleado());
            
            Fichaje fichajeUpdated = fichajeService.save(fichajeActual);
            response.put("mensaje", "Fichaje actualizado (full) con éxito");
            response.put("fichaje", fichajeUpdated);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("mensaje", "Error en actualización completa");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}