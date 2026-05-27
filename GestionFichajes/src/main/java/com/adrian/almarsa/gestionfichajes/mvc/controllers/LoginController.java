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

/**
 * Controlador encargado de gestionar el proceso de login y logout
 * tanto para administradores como para empleados. 
 * Se encarga de validar credenciales, guardar los datos básicos
 * en sesión y redirigir al área correspondiente según el rol.
 */
@Controller
public class LoginController {
	
	@Autowired
    private LoginService loginService;
	
	/**
     * Muestra la página de login.  
     * Si se recibe un parámetro de error, se añade un mensaje informativo
     * para indicar que las credenciales no fueron válidas.
     *
     * @param error indica si hubo un intento fallido de autenticación
     * @param model modelo para enviar datos a la vista
     * @return vista del formulario de login
     */
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        Model model) {
        // Si venimos de un fallo en auth-check, mostramos el mensaje de error
        if (error != null) {
            model.addAttribute("error", "Email o contraseña incorrectos.");
        }
        return "login";
    }

    /**
     * Procesa las credenciales enviadas desde el formulario de login.
     * El sistema busca al usuario tanto en la tabla de administradores
     * como en la de empleados.  
     * Si las credenciales son correctas, se guarda el ID y el rol en sesión
     * y se redirige al área correspondiente.
     *
     * @param username email introducido por el usuario
     * @param password contraseña introducida
     * @param session sesión HTTP donde se guardará la información del usuario
     * @return redirección al panel correspondiente o al login si falla
     */
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

        // 3. Lógica de redirección según el tipo de empleado
        if (usuario instanceof Admin) {
            Admin a = (Admin) usuario;
            session.setAttribute("adminLogueadoId", a.getId());
            session.setAttribute("rol", "ADMIN");
            return "redirect:/admin/index"; 
        } 
        
        if (usuario instanceof Empleado) {
            Empleado e = (Empleado) usuario;
            session.setAttribute("usuarioLogueadoId", e.getId());
            session.setAttribute("rol", "EMPLEADO");
            return "redirect:/index"; 
        }
        return "redirect:/login?error=true";
    }
    
    /**
     * Cierra la sesión del usuario eliminando todos los datos almacenados
     * y lo redirige nuevamente a la pantalla de login.
     *
     * @param session sesión actual del usuario
     * @return redirección al login tras cerrar sesión
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Borramos todos los datos de la "mochila" (el ID guardado)
        session.invalidate(); 
        return "redirect:/login?logout";
    }
}
