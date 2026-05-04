package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Admin;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import jakarta.servlet.http.HttpSession;

import com.adrian.almarsa.gestionfichajes.mvc.models.services.LoginService;

@Controller
public class LoginController {
	
	@Autowired
    private LoginService loginService;
    // Muestra el formulario de login
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        Model model) {
        // Si venimos de un fallo en auth-check, mostramos el mensaje de error
        if (error != null) {
            model.addAttribute("error", "Email o contraseña incorrectos.");
        }
        return "login";
    }

    // Procesa las credenciales enviadas por POST
    @PostMapping("/auth-check")
    public String autenticar(@RequestParam String username, 
                             @RequestParam String password, 
                             HttpSession session) {
        
        // 1. Usamos el servicio que busca en ambas tablas (Admin y Empleado)
    	Object usuario = loginService.loginManual(username, password);;

        // 2. Si es nulo, las credenciales fallaron
        if (usuario == null) {
            return "redirect:/login?error=true";
        }

        // 3. LÓGICA DE REDIRECCIÓN SEGÚN EL TIPO
        if (usuario instanceof Admin) {
            Admin a = (Admin) usuario;
            session.setAttribute("adminLogueadoId", a.getId());
            session.setAttribute("rol", "ADMIN");
            return "redirect:/admin/index"; // <--- Ruta para el administrador
        } 
        
        if (usuario instanceof Empleado) {
            Empleado e = (Empleado) usuario;
            session.setAttribute("usuarioLogueadoId", e.getId());
            session.setAttribute("rol", "EMPLEADO");
            return "redirect:/index"; // <--- Ruta para el empleado
        }

        return "redirect:/login?error=true";
    }
    // Destruye la sesión y saca al usuario del sistema
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Borramos todos los datos de la "mochila" (el ID guardado)
        session.invalidate(); 
        return "redirect:/login?logout";
    }
}
