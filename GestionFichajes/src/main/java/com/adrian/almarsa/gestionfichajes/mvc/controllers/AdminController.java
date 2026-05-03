package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.adrian.almarsa.gestionfichajes.mvc.models.services.IAdminService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IEmpleadoService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

    @Autowired
    private IEmpleadoService empleadoService;
    
    @Autowired
    private IAdminService adminService;

    @GetMapping("/admin/index")
    public String indexAdmin(HttpSession session, Model model) {
        String rol = (String) session.getAttribute("rol");

        if (rol == null || !rol.equals("ADMIN")) {
            return "redirect:/login"; 
        }

        // Recuperamos el ID según quién se haya logueado
        Long adminPuroId = (Long) session.getAttribute("adminLogueadoId");
        Long empAdminId = (Long) session.getAttribute("usuarioLogueadoId");

        if (adminPuroId != null) {
            // Es el Admin de la entidad pura
            model.addAttribute("usuario", adminService.findById(adminPuroId));
        } else if (empAdminId != null) {
            // Es el Empleado con rol administrador
            model.addAttribute("usuario", empleadoService.findById(empAdminId));
        }

        return "admin/index"; 
    }
}