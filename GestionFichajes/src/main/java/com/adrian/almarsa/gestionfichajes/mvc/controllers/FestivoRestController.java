package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Festivo;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IFestivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/festivos")
public class FestivoRestController {

    @Autowired
    private IFestivoService festivoService;

    @GetMapping("/eventos")
    public List<Map<String, Object>> getFestivos(@RequestParam(required = false) Long empleadoId) {
        // Si no hay ID, devolvemos lista vacía para evitar errores
        if (empleadoId == null) {
            return new ArrayList<>(); 
        }
        
        List<Festivo> festivos = festivoService.findByEmpleado(empleadoId);
        
        return festivos.stream().map(f -> {
            Map<String, Object> evento = new HashMap<>();
            evento.put("title", "Festivo: " + f.getDescripcion());
            evento.put("start", f.getFecha().toString());
            evento.put("display", "background");
            evento.put("backgroundColor", "#ffc107");
            evento.put("allDay", true);
            evento.put("groupId", "festivos"); 
            return evento; 
        }).collect(Collectors.toList());
    }
}