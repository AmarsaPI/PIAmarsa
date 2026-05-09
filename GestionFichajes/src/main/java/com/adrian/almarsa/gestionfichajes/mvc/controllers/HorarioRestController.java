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
    public ResponseEntity<?> create(@Valid @RequestBody Horario horario, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            Horario horarioNew = horarioService.save(horario);
            response.put("mensaje", "Horario real creado con éxito");
            response.put("horario", horarioNew);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al insertar el horario en la base de datos");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
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

    // 6. Endpoint para el Calendario (FullCalendar) del empleado logueado
    @GetMapping("/horarios-reales/mis-turnos")
    public ResponseEntity<?> misTurnos(
            @RequestParam(value = "start", required = false) String start,
            @RequestParam(value = "end", required = false) String end,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        Empleado empleado = (Empleado) session.getAttribute("usuarioLogueado");

        if (empleado == null) {
            response.put("mensaje", "No hay sesión activa");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        try {
            List<Horario> horarios = horarioService.findByEmpleado(empleado.getId());
            
            // Transformación al formato que FullCalendar requiere
            List<Map<String, Object>> eventos = horarios.stream().map(h -> {
                Map<String, Object> evento = new HashMap<>();
                evento.put("id", h.getId());
                evento.put("title", "Turno");
                evento.put("start", h.getFecha().toString() + "T" + h.getHoraInicio());
                evento.put("end", h.getFecha().toString() + "T" + h.getHoraFin());
                
                // Si la fecha es festiva (puedes añadir lógica aquí), cambiar color
                evento.put("backgroundColor", "#4e73df");
                
                return evento;
            }).collect(Collectors.toList());

            return new ResponseEntity<>(eventos, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al cargar los turnos del calendario");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}