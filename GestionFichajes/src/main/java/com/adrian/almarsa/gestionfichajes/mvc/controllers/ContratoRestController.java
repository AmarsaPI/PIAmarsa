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

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api")
public class ContratoRestController {

    @Autowired
    private IContratoService contratoService;

    // Crea un nuevo contrato (Como no usamos BindingResult aquí, capturamos errores de base de datos)
    @PostMapping("/contratos")
    public ResponseEntity<?> create(@RequestBody Contrato contrato) {
        
        Map<String, Object> response = new HashMap<>();
        Contrato contratoNew;

        try {
            contratoNew = contratoService.guardarContrato(contrato);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al guardar el contrato en la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "Contrato creado con éxito");
        response.put("contrato", contratoNew);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Obtiene el listado de contratos de un empleado por su ID
    @GetMapping("/contratos/empleado/{empleadoId}")
    public ResponseEntity<?> listarContratosPorEmpleado(@PathVariable Long empleadoId) {
        
        Map<String, Object> response = new HashMap<>();
        List<Contrato> contratos = null;

        try {
            Empleado empleado = new Empleado();
            empleado.setId(empleadoId);
            contratos = contratoService.obtenerContratosPorEmpleado(empleado);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (contratos == null || contratos.isEmpty()) {
            response.put("mensaje", "El empleado con ID: " + empleadoId + " no tiene contratos registrados.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(contratos, HttpStatus.OK);
    }

    // Obtiene el contrato activo de un empleado en una fecha concreta
    // Se llamaría así: /api/contratos/activo?empleadoId=1&fecha=2026-05-19
    @GetMapping("/contratos/activo")
    public ResponseEntity<?> obtenerContratoActivo(
            @RequestParam Long empleadoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        Map<String, Object> response = new HashMap<>();
        Contrato contrato = null;

        try {
            Empleado empleado = new Empleado();
            empleado.setId(empleadoId);
            contrato = contratoService.obtenerContratoActivo(empleado, fecha);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al consultar el contrato activo en la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (contrato == null) {
            response.put("mensaje", "No se encontró ningún contrato activo para el empleado ID: " + empleadoId + " en la fecha: " + fecha);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(contrato, HttpStatus.OK);
    }

    // Elimina un contrato por su ID
    @DeleteMapping("/contratos/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();

        try {
            contratoService.eliminarContrato(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar el contrato de la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "Contrato eliminado con éxito");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}