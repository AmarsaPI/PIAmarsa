package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IEmpleadoService;

import jakarta.validation.Valid;

// Controlador REST para gestionar las operaciones CRUD de los empleados
@CrossOrigin(origins = {"*"}) // Permite peticiones desde cualquier origen (CORS)
@RestController
@RequestMapping("/api")
public class EmpleadoRestController {

    @Autowired
    private IEmpleadoService empleadoService;

    // Obtiene el listado completo de empleados
    @GetMapping("/empleados")
    public List<Empleado> index() {
        return empleadoService.findAll();
    }

    // Busca un empleado por ID con manejo de errores de base de datos y existencia
    @GetMapping("/empleados/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {

        Empleado empleado = null;
        Map<String, Object> response = new HashMap<>();

        try {
            empleado = empleadoService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (empleado == null) {
            response.put("mensaje", "El empleado ID: ".concat(id.toString()).concat(" no existe en la base de datos"));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(empleado, HttpStatus.OK);
    }

    // Crea un nuevo empleado validando los campos obligatorios y cifrando la contraseña
    @PostMapping("/empleados")
    public ResponseEntity<?> create(@Valid @RequestBody Empleado empleado, BindingResult result) {

        Map<String, Object> response = new HashMap<>();
        Empleado empleadoNew;

        // Validación de campos de la entidad (definidos en Empleado.java)
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            // El service se encarga de aplicar BCrypt a la password antes de guardar
            empleadoNew = empleadoService.save(empleado);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al guardar el empleado");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "Empleado creado con éxito");
        response.put("empleado", empleadoNew);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    // Actualiza un empleado existente. Nota: volverá a cifrar la password si se envía
    @PutMapping("/empleados/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Empleado empleado,
                                     BindingResult result,
                                     @PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            Empleado empleadoActual = empleadoService.findById(id);

            if (empleadoActual == null) {
                response.put("mensaje", "El empleado ID: " + id + " no existe");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Actualización de los datos del objeto gestionado por JPA
            empleadoActual.setNombre(empleado.getNombre());
            empleadoActual.setEmail(empleado.getEmail());
            empleadoActual.setPassword(empleado.getPassword()); // El service cifrará la nueva pass
            empleadoActual.setRol(empleado.getRol());

            Empleado empleadoUpdated = empleadoService.save(empleadoActual);

            response.put("mensaje", "Empleado actualizado con éxito");
            response.put("fichaje", empleadoUpdated);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar el empleado");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Elimina un empleado por su identificador
    @DeleteMapping("/empleados/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();

        try {
            empleadoService.delete(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar el empleado");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "Empleado eliminado con éxito");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}