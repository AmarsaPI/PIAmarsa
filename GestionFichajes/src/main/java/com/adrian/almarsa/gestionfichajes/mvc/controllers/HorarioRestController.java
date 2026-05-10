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
import org.springframework.web.bind.annotation.*;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Horario;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IHorarioService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"*"})
public class HorarioRestController {

    @Autowired
    private IHorarioService horarioService;

    // 1. Listado global de todos los horarios reales
    @GetMapping("/horarios-reales")
    public List<Horario> index() {
        return horarioService.findAll();
    }

    // 2. Buscar un horario real por ID
    @GetMapping("/horarios-reales/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Horario horario = horarioService.findById(id);
            if (horario == null) {
                response.put("mensaje", "El horario real ID: " + id + " no existe");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(horario, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al consultar el horario en la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 3. Crear un nuevo horario real (Asignación manual en el calendario)
    @PostMapping("/horarios-reales")
    public ResponseEntity<?> guardar(@RequestBody Horario horario) {
        try {
            // Guardamos el horario
            horarioService.save(horario);
            
            // Creamos una respuesta limpia
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Horario guardado con éxito");
            respuesta.put("idGenerado", horario.getId());
            
            return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
            
        } catch (Exception e) {
            // Log del error para que tú lo veas en la consola
            e.printStackTrace(); 
            
            Map<String, Object> error = new HashMap<>();
            error.put("mensaje", "Error al guardar el horario");
            error.put("detalles", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 4. Actualizar un horario real
    @PutMapping("/horarios-reales/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Horario horario, BindingResult result, @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            Horario horarioActual = horarioService.findById(id);
            if (horarioActual == null) {
                response.put("mensaje", "El horario real ID: " + id + " no existe");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Actualizamos los campos específicos de la fecha real
            horarioActual.setFecha(horario.getFecha());
            horarioActual.setHoraInicio(horario.getHoraInicio());
            horarioActual.setHoraFin(horario.getHoraFin());
            horarioActual.setEmpleado(horario.getEmpleado());
            // Si añadiste el campo 'tipo' o 'festivo', actualízalo aquí también

            Horario horarioUpdated = horarioService.save(horarioActual);
            response.put("mensaje", "Horario real actualizado con éxito");
            response.put("horario", horarioUpdated);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar el horario en la base de datos");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 5. Eliminar un horario real
    @DeleteMapping("/horarios-reales/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (horarioService.findById(id) == null) {
                response.put("mensaje", "El horario real ID: " + id + " no existe");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            horarioService.delete(id);
            response.put("mensaje", "Horario real eliminado con éxito");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar el horario de la base de datos");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/horarios-reales/mis-turnos")
    public ResponseEntity<?> misTurnos(
            HttpSession session,
            @RequestParam Map<String, String> allParams) { // Atrapa start y end sin validaciones estrictas
        
        Long empleadoId = (Long) session.getAttribute("usuarioLogueadoId");
        
        if (empleadoId == null) {
            return new ResponseEntity<>(Map.of("mensaje", "No hay sesión"), HttpStatus.UNAUTHORIZED);
        }

        // Buscamos los horarios del empleado
        List<Horario> listaHorarios = horarioService.findByEmpleado(empleadoId);
        List<Map<String, Object>> eventos = new java.util.ArrayList<>(); 

        for (Horario h : listaHorarios) {
            // Bloque 1
            Map<String, Object> turno1 = new HashMap<>();
            turno1.put("start", h.getFecha().toString());
            turno1.put("title", h.getHoraInicio() + " - " + h.getHoraFin());
            turno1.put("backgroundColor", "#d1ecf1");
            turno1.put("textColor", "#0c5460");
            // Importante: Meterlo en un mapa llamado extendedProps para el JS
            Map<String, Object> props1 = new HashMap<>();
            props1.put("textoPersonalizado", h.getHoraInicio() + " a " + h.getHoraFin());
            turno1.put("extendedProps", props1);
            
            eventos.add(turno1);

            // Bloque 2 (Partido)
            if (h.getHoraInicio2() != null) { 
                Map<String, Object> turno2 = new HashMap<>();
                turno2.put("start", h.getFecha().toString());
                turno2.put("title", h.getHoraInicio2() + " - " + h.getHoraFin2());
                turno2.put("backgroundColor", "#fff3cd");
                turno2.put("textColor", "#856404");
                
                Map<String, Object> props2 = new HashMap<>();
                props2.put("textoPersonalizado", h.getHoraInicio2() + " a " + h.getHoraFin2());
                turno2.put("extendedProps", props2);
                
                eventos.add(turno2);
            }
        }
        return new ResponseEntity<>(eventos, HttpStatus.OK);
    }
}