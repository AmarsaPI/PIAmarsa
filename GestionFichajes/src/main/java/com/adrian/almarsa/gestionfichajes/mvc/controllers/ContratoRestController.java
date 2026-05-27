package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import java.time.LocalDate;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Contrato;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IContratoService;

/**
 * Controlador REST encargado de gestionar los contratos.
 * Permite crear, consultar y eliminar contratos.
 */
@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api")
public class ContratoRestController {

    @Autowired
    private IContratoService contratoService;

    /**
     * Guarda un nuevo contrato.
     * 
     * @param contrato datos del contrato
     * @return respuesta con el contrato creado
     */

    // Crear un nuevo contrato
    // POST: /api/contratos
    @PostMapping("/contratos")
    public ResponseEntity<?> create(@RequestBody Contrato contrato) {
        
        Map<String, Object> response = new HashMap<>();

        Contrato contratoNew;

        try {

            contratoNew = contratoService.guardarContrato(contrato);

        } catch (DataAccessException e) {

            response.put(
                    "mensaje",
                    "Error al guardar el contrato en la base de datos"
            );

            response.put("error", e.getMessage());

            return new ResponseEntity<>(
                    response,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        response.put("mensaje", "Contrato creado correctamente");

        response.put("contrato", contratoNew);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Obtiene todos los contratos de un empleado.
     * 
     * @param empleadoId id del empleado
     * @return lista de contratos
     */

    // Obtener los contratos de un empleado
    // GET: /api/contratos/empleado/{empleadoId}
    @GetMapping("/contratos/empleado/{empleadoId}")
    public ResponseEntity<?> listarContratosPorEmpleado(
            @PathVariable Long empleadoId) {
        
        Map<String, Object> response = new HashMap<>();

        List<Contrato> contratos = null;

        try {

            Empleado empleado = new Empleado();

            empleado.setId(empleadoId);

            contratos =
                    contratoService.obtenerContratosPorEmpleado(empleado);

        } catch (DataAccessException e) {

            response.put(
                    "mensaje",
                    "Error al consultar los contratos"
            );

            response.put("error", e.getMessage());

            return new ResponseEntity<>(
                    response,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        // Comprueba si el empleado tiene contratos
        if (contratos == null || contratos.isEmpty()) {

            response.put(
                    "mensaje",
                    "El empleado no tiene contratos registrados"
            );

            return new ResponseEntity<>(
                    response,
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(contratos, HttpStatus.OK);
    }

    /**
     * Obtiene el contrato activo de un empleado
     * en una fecha concreta.
     * 
     * @param empleadoId id del empleado
     * @param fecha fecha a comprobar
     * @return contrato activo encontrado
     */

    // Obtener el contrato activo de un empleado
    // GET: /api/contratos/activo?empleadoId=1&fecha=2026-05-19
    @GetMapping("/contratos/activo")
    public ResponseEntity<?> obtenerContratoActivo(

            @RequestParam Long empleadoId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha) {
        
        Map<String, Object> response = new HashMap<>();

        Contrato contrato = null;

        try {

            Empleado empleado = new Empleado();

            empleado.setId(empleadoId);

            contrato =
                    contratoService.obtenerContratoActivo(empleado, fecha);

        } catch (DataAccessException e) {

            response.put(
                    "mensaje",
                    "Error al consultar el contrato activo"
            );

            response.put("error", e.getMessage());

            return new ResponseEntity<>(
                    response,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        // Comprueba si existe un contrato activo
        if (contrato == null) {

            response.put(
                    "mensaje",
                    "No se encontró ningún contrato activo"
            );

            return new ResponseEntity<>(
                    response,
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(contrato, HttpStatus.OK);
    }

    /**
     * Elimina un contrato por su id.
     * 
     * @param id id del contrato
     * @return mensaje de confirmación
     */

    // Eliminar un contrato
    // DELETE: /api/contratos/{id}
    @DeleteMapping("/contratos/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();

        try {

            contratoService.eliminarContrato(id);

        } catch (DataAccessException e) {

            response.put(
                    "mensaje",
                    "Error al eliminar el contrato"
            );

            response.put("error", e.getMessage());

            return new ResponseEntity<>(
                    response,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        response.put("mensaje", "Contrato eliminado correctamente");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}