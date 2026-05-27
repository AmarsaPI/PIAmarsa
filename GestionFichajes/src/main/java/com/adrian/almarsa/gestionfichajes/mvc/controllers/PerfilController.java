package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IEmpleadoService;

import jakarta.servlet.http.HttpSession;

/**
 * Controlador encargado de mostrar la información del perfil del empleado
 * que ha iniciado sesión.  
 * Obtiene el ID guardado en sesión y carga los datos del usuario
 * para mostrarlos en la vista correspondiente.
 */
@Controller
public class PerfilController {

    @Autowired 
    private IEmpleadoService empleadoService;

    /**
     * Muestra la página de perfil del usuario logueado.  
     * Recupera el ID almacenado en sesión y carga los datos completos
     * del empleado para enviarlos a la vista.
     *
     * @param model modelo donde se añade el usuario a mostrar
     * @param session sesión actual del usuario
     * @return vista del perfil del empleado
     */
    @GetMapping("/perfil")
    public String verPerfil(Model model, HttpSession session) {
        Long id = (Long) session.getAttribute("usuarioLogueadoId");
        model.addAttribute("usuario", empleadoService.findById(id));
        return "perfil";
    }
}

