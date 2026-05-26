package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import java.util.*;
import java.util.stream.Collectors;

import com.adrian.almarsa.gestionfichajes.mvc.models.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IEmpleadoService;

import jakarta.validation.Valid;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api")
public class EmpleadoRestController {

    @Autowired
    private IEmpleadoService empleadoService;
    
    private JwtService jwtService = new JwtService();

    @GetMapping("/empleados")
    public List<Empleado> index() {
        return empleadoService.findAll();
    }

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
            response.put("mensaje", "El ID: ".concat(id.toString()).concat(" no existe en la base de datos"));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(empleado, HttpStatus.OK);
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login/{email}/{password}")
    public ResponseEntity<?> showEmail(@PathVariable String email, @PathVariable String password) {
        Empleado empleado = null;
        Map<String, Object> response = new HashMap<>();
        try {
            empleado = empleadoService.findByEmail(email);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (empleado == null) {
            response.put("mensaje", "El email: ".concat(email.toString()).concat(" no existe en la base de datos"));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else if (!passwordEncoder.matches(password, empleado.getPassword())) {
            response.put("mensaje", "Contraseña incorrecta");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
        JwtService jwtService = new JwtService();
        String token = jwtService.generarToken(email, empleado.getId(), empleado.getRol());
        return new ResponseEntity<>(Map.of("token", token, "empleado", empleado), HttpStatus.OK);
    }

    @PostMapping("/empleados")
    public ResponseEntity<?> create(@Valid @RequestBody Empleado empleado, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        Empleado empleadoNew;
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        try {
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

    @PutMapping("/empleados/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Empleado empleado, BindingResult result, @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
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
            empleadoActual.setNombre(empleado.getNombre());
            empleadoActual.setEmail(empleado.getEmail());
            empleadoActual.setPassword(empleado.getPassword());
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

    @GetMapping("/login/simple/{email}/{password}")
    public ResponseEntity<?> showEmailSimple(@PathVariable String email, @PathVariable String password) {
        // Esta es la implementación del login sin JWT que aparecía en tu segunda clase
        Empleado empleado = null;
        Map<String, Object> response = new HashMap<>();
        try {
            empleado = empleadoService.findByEmail(email);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (empleado == null) {
            response.put("mensaje", "El email: ".concat(email).concat(" no existe en la base de datos"));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else if (!passwordEncoder.matches(password, empleado.getPassword())) {
            response.put("mensaje", "Contraseña incorrecta");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(empleado, HttpStatus.OK);
    }
}