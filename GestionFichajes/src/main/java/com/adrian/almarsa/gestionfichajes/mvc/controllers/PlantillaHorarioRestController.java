package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.PlantillaHorario;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IPlantillaHorarioService;

import jakarta.validation.Valid;

// Controlador para la gestión de turnos y planificación semanal
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"*"})
public class PlantillaHorarioRestController {

    @Autowired
    private IPlantillaHorarioService plantillaHorarioService;

    // Listado global de todos los horarios configurados
    @GetMapping("/horarios")
    public List<PlantillaHorario> index() {
        return plantillaHorarioService.findAll();
    }

    // Busca un turno específico por su ID
    @GetMapping("/horarios/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            PlantillaHorario horario = plantillaHorarioService.findById(id);
            if (horario == null) {
                response.put("mensaje", "El horario ID: " + id + " no existe");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(horario, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al consultar el horario");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Crea una nueva asignación de horario para un empleado
    @PostMapping("/horarios")
    public ResponseEntity<?> create(@Valid @RequestBody PlantillaHorario horario, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            PlantillaHorario horarioNew = plantillaHorarioService.save(horario);
            response.put("mensaje", "Horario creado con éxito");
            response.put("horario", horarioNew);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al guardar el horario en la DB");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Actualiza los detalles de un horario (día, hora inicio/fin o empleado asignado)
    @PutMapping("/horarios/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody PlantillaHorario horario, BindingResult result, @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            PlantillaHorario horarioActual = plantillaHorarioService.findById(id);
            if (horarioActual == null) {
                response.put("mensaje", "El horario ID: " + id + " no existe");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            horarioActual.setDiaSemana(horario.getDiaSemana());
            horarioActual.setHoraInicio(horario.getHoraInicio());
            horarioActual.setHoraFin(horario.getHoraFin());
            horarioActual.setEmpleado(horario.getEmpleado());

            PlantillaHorario horarioUpdated = plantillaHorarioService.save(horarioActual);
            response.put("mensaje", "Horario actualizado con éxito");
            response.put("horario", horarioUpdated);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar el horario");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Elimina un registro de horario
    @DeleteMapping("/horarios/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (plantillaHorarioService.findById(id) == null) {
                response.put("mensaje", "El horario ID: " + id + " no existe");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            plantillaHorarioService.delete(id);
            response.put("mensaje", "Horario eliminado con éxito");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar el horario");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Endpoint estratégico: devuelve el calendario semanal de un empleado concreto
    @GetMapping("/horarios/empleado/{empleadoId}")
    public ResponseEntity<?> horariosPorEmpleado(@PathVariable Long empleadoId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<PlantillaHorario> horarios = plantillaHorarioService.findByEmpleado(empleadoId);
            if (horarios.isEmpty()) {
                response.put("mensaje", "No hay horarios para el empleado ID: " + empleadoId);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(horarios, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al consultar horarios");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}