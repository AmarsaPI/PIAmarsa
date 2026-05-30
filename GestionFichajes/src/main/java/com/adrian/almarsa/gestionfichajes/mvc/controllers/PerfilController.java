package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
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

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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

    @PostMapping("/perfil/password")
    public String cambiarPassword(
        @RequestParam String actual,
        @RequestParam String nueva,
        @RequestParam String repetir,
        HttpSession session,
        RedirectAttributes flash) {

    Long id = (Long) session.getAttribute("usuarioLogueadoId");
    Empleado emp = empleadoService.findById(id);

    if (!passwordEncoder.matches(actual, emp.getPassword())) {
        flash.addFlashAttribute("error", "La contraseña actual no es correcta.");
        return "redirect:/perfil";
    }
    if (!nueva.equals(repetir)) {
        flash.addFlashAttribute("error", "Las contraseñas nuevas no coinciden.");
        return "redirect:/perfil";
    }
    if (nueva.length() < 6) {
        flash.addFlashAttribute("error", "Mínimo 6 caracteres.");
        return "redirect:/perfil";
    }

    empleadoService.actualizarPassword(id, nueva);
    flash.addFlashAttribute("success", "Contraseña actualizada.");
    return "redirect:/perfil";
}

    
}

