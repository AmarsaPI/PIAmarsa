package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import java.util.ArrayList;
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

/**
 * Controlador REST encargado de gestionar las plantillas de horarios semanales.
 * Permite crear, consultar, actualizar y eliminar turnos base, así como obtener
 * una vista previa completa de una plantilla por nombre.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"*"})
public class PlantillaHorarioRestController {

    @Autowired
    private IPlantillaHorarioService plantillaService;

    /**
     * Devuelve el listado completo de horarios configurados en la plantilla.
     *
     * @return lista de todos los registros de plantilla
     */
    @GetMapping("/horarios")
    public List<PlantillaHorario> index() {
        return plantillaService.findAll();
    }

    /**
     * Busca un horario de plantilla por su ID.
     *
     * @param id identificador del horario
     * @return el horario encontrado o un mensaje si no existe
     */
    @GetMapping("/horarios/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            PlantillaHorario horario = plantillaService.findById(id);
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
    
    /**
     * Crea un nuevo registro de plantilla (día + horas).
     * Valida los datos antes de guardarlos.
     *
     * @param horario datos del turno a crear
     * @param result resultado de la validación
     * @return mensaje de éxito o errores de validación
     */
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
            PlantillaHorario horarioNew = plantillaService.save(horario);
            response.put("mensaje", "Horario creado con éxito");
            response.put("horario", horarioNew);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al guardar el horario en la DB");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Actualiza un turno de plantilla existente.
     * Permite modificar el día de la semana y las horas asignadas.
     *
     * @param horario datos actualizados
     * @param result validación de campos
     * @param id ID del turno a modificar
     * @return mensaje de éxito o error
     */
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
            PlantillaHorario horarioActual = plantillaService.findById(id);
            if (horarioActual == null) {
                response.put("mensaje", "El horario ID: " + id + " no existe");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            horarioActual.setDiaSemana(horario.getDiaSemana());
            horarioActual.setHoraInicio(horario.getHoraInicio());
            horarioActual.setHoraFin(horario.getHoraFin());

            PlantillaHorario horarioUpdated = plantillaService.save(horarioActual);
            response.put("mensaje", "Horario actualizado con éxito");
            response.put("horario", horarioUpdated);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar el horario");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Elimina un turno de plantilla por su ID.
     *
     * @param id identificador del turno
     * @return mensaje indicando si se eliminó correctamente
     */
    @DeleteMapping("/horarios/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (plantillaService.findById(id) == null) {
                response.put("mensaje", "El horario ID: " + id + " no existe");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            plantillaService.delete(id);
            response.put("mensaje", "Horario eliminado con éxito");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar el horario");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Obtiene una vista previa de todos los turnos pertenecientes a una plantilla,
     * usando como referencia el nombre de la plantilla del registro solicitado.
     *
     * @param id ID de cualquier turno perteneciente a la plantilla
     * @return nombre de la plantilla y lista de turnos ordenados por día
     */
    @GetMapping("/plantillas/{id}/preview")
    public ResponseEntity<?> obtenerDetallePlantilla(@PathVariable Long id) {
        PlantillaHorario registroBase = plantillaService.findById(id); 
        
        if (registroBase == null || registroBase.getNombrePlantilla() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        String nombreABuscar = registroBase.getNombrePlantilla();
        
        List<PlantillaHorario> diasDeLaPlantilla = plantillaService.findByNombrePlantilla(nombreABuscar);
        
        List<Map<String, Object>> turnosLimpios = new ArrayList<>();
        for (PlantillaHorario ph : diasDeLaPlantilla) {
            Map<String, Object> turno = new HashMap<>();
            turno.put("diaSemana", ph.getDiaSemana().getValue()); 
            turno.put("horaInicio", ph.getHoraInicio().toString()); 
            turno.put("horaFin", ph.getHoraFin().toString()); 
            
            turnosLimpios.add(turno);
        }
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("nombrePlantilla", nombreABuscar);
        respuesta.put("turnos", turnosLimpios); 
        
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }
    
    /**
     * Elimina todos los turnos asociados a una plantilla según su nombre.
     * Útil para borrar plantillas completas como "Mañana", "Tarde", etc.
     *
     * @param nombre nombre de la plantilla a eliminar
     * @return mensaje indicando el resultado de la operación
     */
    @DeleteMapping("/plantillas/nombre/{nombre}")
    public ResponseEntity<?> eliminarPlantillaPorNombre(@PathVariable String nombre) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Buscamos todas las filas que se llamen igual (ej: los 5 días de la plantilla "Tarde")
            List<PlantillaHorario> diasAEliminar = plantillaService.findByNombrePlantilla(nombre);
            
            if (diasAEliminar.isEmpty()) {
                response.put("mensaje", "La plantilla '" + nombre + "' no existe.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Eliminamos fila por fila usando el servicio que ya tienes
            for (PlantillaHorario ph : diasAEliminar) {
                plantillaService.delete(ph.getId());
            }

            response.put("mensaje", "Plantilla '" + nombre + "' eliminada por completo con éxito");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar la plantilla de la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}