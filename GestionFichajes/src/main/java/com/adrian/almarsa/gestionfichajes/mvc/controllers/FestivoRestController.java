package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Festivo;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IFestivoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador REST encargado de devolver
 * los festivos para el calendario.
 */
@RestController
@RequestMapping("/api/festivos")
public class FestivoRestController {

    @Autowired
    private IFestivoService festivoService;

    /**
     * Obtiene los festivos de un empleado
     * y los transforma en eventos para el calendario.
     * 
     * @param empleadoId id del empleado
     * @return lista de eventos de festivos
     */

    // Obtener festivos para mostrarlos en el calendario
    @GetMapping("/eventos")
    public List<Map<String, Object>> getFestivos(
            @RequestParam(required = false) Long empleadoId) {

        // Si no llega el id devuelve una lista vacía
        if (empleadoId == null) {

            return new ArrayList<>();
        }
        
        // Busca los festivos del empleado
        List<Festivo> festivos =
                festivoService.findByEmpleado(empleadoId);
        
        // Convierte cada festivo en un evento del calendario
        return festivos.stream().map(f -> {

            Map<String, Object> evento = new HashMap<>();

            evento.put(
                    "title",
                    "Festivo: " + f.getDescripcion()
            );

            evento.put(
                    "start",
                    f.getFecha().toString()
            );

            evento.put("display", "background");

            evento.put("backgroundColor", "#ffc107");

            evento.put("allDay", true);

            evento.put("groupId", "festivos");

            return evento;

        }).collect(Collectors.toList());
    }
}