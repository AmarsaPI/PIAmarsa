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

import com.adrian.almarsa.gestionfichajes.mvc.models.dto.EmpleadoLoginDTO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IEmpleadoService;

import jakarta.validation.Valid;

/**
 * Controlador REST encargado de gestionar empleados
 * y autenticación mediante JWT.
 */
@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api")
public class EmpleadoRestController {

    @Autowired
    private IEmpleadoService empleadoService;
    
    private JwtService jwtService = new JwtService();

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Obtiene todos los empleados.
     * 
     * @return lista de empleados
     */

    // Obtener todos los empleados
    @GetMapping("/empleados")
    public List<Empleado> index() {

        return empleadoService.findAll();
    }

    /**
     * Obtiene un empleado por su id.
     * 
     * @param id id del empleado
     * @return empleado encontrado
     */

    // Buscar un empleado por id
    @GetMapping("/empleados/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {

        Empleado empleado = null;

        Map<String, Object> response = new HashMap<>();

        try {

            empleado = empleadoService.findById(id);

        } catch (DataAccessException e) {

            response.put(
                    "mensaje",
                    "Error al consultar la base de datos"
            );

            response.put("error", e.getMessage());

            return new ResponseEntity<>(
                    response,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        // Comprueba si el empleado existe
        if (empleado == null) {

            response.put(
                    "mensaje",
                    "El empleado no existe"
            );

            return new ResponseEntity<>(
                    response,
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(empleado, HttpStatus.OK);
    }

    /**
     * Realiza el login del empleado y genera un token JWT.
     * 
     * @param email email del empleado
     * @param password contraseña del empleado
     * @return token JWT y datos del empleado
     */

    // Login con generación de token JWT
    @PostMapping("/login/{email}/{password}")
    public ResponseEntity<?> showEmail(@PathVariable String email,
                                       @PathVariable String password) {

        Empleado empleado = null;

        Map<String, Object> response = new HashMap<>();

        try {

            empleado = empleadoService.findByEmail(email);

        } catch (DataAccessException e) {

            response.put(
                    "mensaje",
                    "Error al consultar la base de datos"
            );

            response.put("error", e.getMessage());

            return new ResponseEntity<>(
                    response,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        // Comprueba si el email existe
        if (empleado == null) {

            response.put(
                    "mensaje",
                    "El email no existe"
            );

            return new ResponseEntity<>(
                    response,
                    HttpStatus.NOT_FOUND
            );

        // Comprueba la contraseña
        } else if (!passwordEncoder.matches(
                password,
                empleado.getPassword())) {

            response.put(
                    "mensaje",
                    "Contraseña incorrecta"
            );

            return new ResponseEntity<>(
                    response,
                    HttpStatus.FORBIDDEN
            );
        }

        // Genera el token JWT
        JwtService jwtService = new JwtService();

        String token =
                jwtService.generarToken(
                        email,
                        empleado.getId(),
                        empleado.getRol()
                );

        return ResponseEntity.ok(Map.of(
                "token", token, 
                "empleado", new EmpleadoLoginDTO(empleado)
        ));
    }

    /**
     * Crea un nuevo empleado.
     * 
     * @param empleado datos del empleado
     * @param result errores de validación
     * @return empleado creado
     */

    // Crear un nuevo empleado
    @PostMapping("/empleados")
    public ResponseEntity<?> create(@Valid @RequestBody Empleado empleado,
                                    BindingResult result) {

        Map<String, Object> response = new HashMap<>();

        Empleado empleadoNew;

        // Comprueba errores de validación
        if (result.hasErrors()) {

            List<String> errors =
                    result.getFieldErrors().stream()

                    .map(err ->
                            "El campo '"
                                    + err.getField()
                                    + "' "
                                    + err.getDefaultMessage()
                    )

                    .collect(Collectors.toList());

            response.put("errors", errors);

            return new ResponseEntity<>(
                    response,
                    HttpStatus.BAD_REQUEST
            );
        }

        try {

            empleadoNew = empleadoService.save(empleado);

        } catch (DataAccessException e) {

            response.put(
                    "mensaje",
                    "Error al guardar el empleado"
            );

            response.put("error", e.getMessage());

            return new ResponseEntity<>(
                    response,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        response.put(
                "mensaje",
                "Empleado creado correctamente"
        );

        response.put("empleado", empleadoNew);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    /**
     * Actualiza un empleado existente.
     * 
     * @param empleado datos actualizados
     * @param result errores de validación
     * @param id id del empleado
     * @return empleado actualizado
     */

    // Actualizar un empleado
    @PutMapping("/empleados/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Empleado empleado,
                                    BindingResult result,
                                    @PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();

        // Comprueba errores de validación
        if (result.hasErrors()) {

            List<String> errors =
                    result.getFieldErrors().stream()

                    .map(err ->
                            "El campo '"
                                    + err.getField()
                                    + "' "
                                    + err.getDefaultMessage()
                    )

                    .collect(Collectors.toList());

            response.put("errors", errors);

            return new ResponseEntity<>(
                    response,
                    HttpStatus.BAD_REQUEST
            );
        }

        try {

            Empleado empleadoActual =
                    empleadoService.findById(id);

            // Comprueba si el empleado existe
            if (empleadoActual == null) {

                response.put(
                        "mensaje",
                        "El empleado no existe"
                );

                return new ResponseEntity<>(
                        response,
                        HttpStatus.NOT_FOUND
                );
            }

            empleadoActual.setNombre(empleado.getNombre());

            empleadoActual.setEmail(empleado.getEmail());

            empleadoActual.setPassword(empleado.getPassword());

            empleadoActual.setRol(empleado.getRol());

            Empleado empleadoUpdated =
                    empleadoService.save(empleadoActual);

            response.put(
                    "mensaje",
                    "Empleado actualizado correctamente"
            );

            response.put("fichaje", empleadoUpdated);

            return new ResponseEntity<>(
                    response,
                    HttpStatus.OK
            );

        } catch (DataAccessException e) {

            response.put(
                    "mensaje",
                    "Error al actualizar el empleado"
            );

            response.put("error", e.getMessage());

            return new ResponseEntity<>(
                    response,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Elimina un empleado por su id.
     * 
     * @param id id del empleado
     * @return mensaje de confirmación
     */

    // Eliminar un empleado
    @DeleteMapping("/empleados/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();

        try {

            empleadoService.delete(id);

        } catch (DataAccessException e) {

            response.put(
                    "mensaje",
                    "Error al eliminar el empleado"
            );

            response.put("error", e.getMessage());

            return new ResponseEntity<>(
                    response,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        response.put(
                "mensaje",
                "Empleado eliminado correctamente"
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    /**
     * Realiza un login simple sin usar JWT.
     * 
     * @param email email del empleado
     * @param password contraseña del empleado
     * @return datos del empleado
     */

    // Login simple sin token JWT
    @GetMapping("/login/simple/{email}/{password}")
    public ResponseEntity<?> showEmailSimple(@PathVariable String email,
                                             @PathVariable String password) {

        Empleado empleado = null;

        Map<String, Object> response = new HashMap<>();

        try {

            empleado = empleadoService.findByEmail(email);

        } catch (DataAccessException e) {

            response.put(
                    "mensaje",
                    "Error al consultar la base de datos"
            );

            response.put("error", e.getMessage());

            return new ResponseEntity<>(
                    response,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        // Comprueba si el empleado existe
        if (empleado == null) {

            response.put(
                    "mensaje",
                    "El email no existe"
            );

            return new ResponseEntity<>(
                    response,
                    HttpStatus.NOT_FOUND
            );

        // Comprueba la contraseña
        } else if (!passwordEncoder.matches(
                password,
                empleado.getPassword())) {

            response.put(
                    "mensaje",
                    "Contraseña incorrecta"
            );

            return new ResponseEntity<>(
                    response,
                    HttpStatus.FORBIDDEN
            );
        }

        return new ResponseEntity<>(empleado, HttpStatus.OK);
    }
}