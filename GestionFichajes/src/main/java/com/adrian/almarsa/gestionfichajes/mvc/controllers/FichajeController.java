package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Fichaje;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IEmpleadoService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IFichajeService;

import jakarta.servlet.http.HttpSession;

@Controller
public class FichajeController {

    @Autowired private IEmpleadoService empleadoService;
    @Autowired private IFichajeService fichajeService; // Deberás crear este service

    @GetMapping("/fichar")
    public String mostrarFichaje(HttpSession session, Model model) {
        Long id = (Long) session.getAttribute("usuarioLogueadoId");
        if (id == null) return "redirect:/login";

        Empleado usuario = empleadoService.findById(id);
        
        // Buscamos si tiene algún fichaje sin fecha de salida
        Fichaje ultimo = fichajeService.findUltimoSinCerrar(id);
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("ultimoFichaje", ultimo);
        model.addAttribute("enJornada", ultimo != null); // Si hay uno abierto, está en jornada

        return "fichar";
    }

    @PostMapping("/fichar/registrar-entrada")
    public String registrarEntrada(HttpSession session) {
        Long id = (Long) session.getAttribute("usuarioLogueadoId");
        Empleado e = empleadoService.findById(id);
        
        Fichaje nuevo = new Fichaje();
        nuevo.setEmpleado(e);
        fichajeService.registrarEntrada(nuevo); 
        
        return "redirect:/fichar";
    }

    @PostMapping("/fichar/registrar-salida")
    public String registrarSalida(HttpSession session, Model model) {
        Long id = (Long) session.getAttribute("usuarioLogueadoId");
        
        Empleado usuario = empleadoService.findById(id);
        Fichaje abierto = fichajeService.findUltimoSinCerrar(id);
        
        if (abierto != null) {
            abierto.setFechaSalida(LocalDateTime.now());
            fichajeService.save(abierto);
            // Pasamos el mensaje directamente al modelo
            model.addAttribute("mensajeExito", "¡Jornada finalizada con éxito!");
        }

        // Al no haber redirect, tenemos que volver a cargar los datos para la vista
        model.addAttribute("usuario", usuario);
        model.addAttribute("ultimoFichaje", null); // Ya lo hemos cerrado
        model.addAttribute("enJornada", false);

        return "fichar"; // Devolvemos el HTML directamente
    }
}