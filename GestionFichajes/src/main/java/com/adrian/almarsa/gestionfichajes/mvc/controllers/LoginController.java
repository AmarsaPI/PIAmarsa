package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IEmpleadoService;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired 
    private PasswordEncoder passwordEncoder;
    
    @Autowired 
    private IEmpleadoService empleadoService;

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
                             HttpSession session) { // <--- Spring inyecta la sesión aquí
        
        // 1. Buscamos al empleado por su email
        Empleado e = empleadoService.findByEmail(username);

        // 2. Verificamos si existe y si la contraseña (en texto plano) coincide con el hash de la DB
        if (e != null && passwordEncoder.matches(password, e.getPassword())) {
            
            /* PASO CLAVE: Guardamos el ID del empleado en la sesión del servidor.
               Esto crea una "mochila" virtual asociada a este navegador específico.
               El usuario NO ve este ID en su barra de direcciones.
            */
            session.setAttribute("usuarioLogueadoId", e.getId());
            
            // Redirigimos a una ruta limpia, sin parámetros visibles
            return "redirect:/index";
            
        } else {
            // Si falla, volvemos al login con el aviso de error
            return "redirect:/login?error=true";
        }
    }

    // Destruye la sesión y saca al usuario del sistema
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Borramos todos los datos de la "mochila" (el ID guardado)
        session.invalidate(); 
        return "redirect:/login?logout";
    }
}
