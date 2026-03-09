package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Horario;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IEmpleadoService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IHorarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class EmpleadoController {

    @Autowired 
    private IEmpleadoService empleadoService;
    
    @Autowired 
    private IHorarioService horarioService;

    // Página de inicio tras el login
    //HttpSession crea en memoria una sesión que le da al servidor una cookie llamada JSESSIONID(parecido a un token)
    //Así sabremos siempre el usuario que está logueado, otra forma seria hacerlo con un método en sucrityFilterChain
    //llamado processLogin, así no depende del id ni se pasa la pass por la url(más seguro).
    @GetMapping("/index")
    public String mostrarIndex(HttpSession session, Model model) {
        
        /* 1. INTENTO DE RECUPERACIÓN: Intentamos sacar el ID de la sesión.
           Si el usuario no ha pasado por el login, este valor será NULL.
        */
        Long id = (Long) session.getAttribute("usuarioLogueadoId");

        // 2. FILTRO DE SEGURIDAD: Si no hay ID, lo expulsamos al login.
        // Esto evita que alguien entre escribiendo directamente "/index" en la URL.
        if (id == null) {
            return "redirect:/login";
        }

        // 3. CARGA DE DATOS: Como ya tenemos el ID seguro, buscamos al empleado
        Empleado usuario = empleadoService.findById(id);
        
        // 4. PASO A LA VISTA: Enviamos el objeto al HTML de Thymeleaf
        model.addAttribute("usuario", usuario);

        return "index";
    }

    // Página específica de horarios personales
    @GetMapping("/horario_personal")
    public String mostrarHorario(HttpSession session, Model model) {
        
        // Volvemos a pedir el ID a la sesión (nunca a la URL)
        Long id = (Long) session.getAttribute("usuarioLogueadoId");

        // Verificamos que esté logueado
        if (id == null) return "redirect:/login";

        // Buscamos los datos reales vinculados a ese ID
        Empleado usuario = empleadoService.findById(id);
        List<Horario> horariosReales = horarioService.findByEmpleado(id);

        // Preparamos los datos para la tabla del HTML
        model.addAttribute("usuario", usuario);
        model.addAttribute("horarios", horariosReales);
        model.addAttribute("rangoSemana", "Semana Actual");

        return "horario_personal";
    }
    
    @GetMapping("/gestion")
    public String mostrarGestion(HttpSession session, Model model) {
        // NIVEL 1: ¿Está autenticado?
        Long id = (Long) session.getAttribute("usuarioLogueadoId");
        
        if (id == null) {
            // No está autenticado -> Al login
            return "redirect:/login";
        }

        // Buscamos al empleado real en la DB
        Empleado usuario = empleadoService.findById(id);

        // NIVEL 2: ¿Es administrador?
        if (usuario == null || !usuario.getRol().name().equals("ADMINISTRADOR")) {
            // Está autenticado pero NO es admin -> Al index con error
            return "redirect:/index?error=no_autorizado";
        }

        // SI PASA LOS DOS FILTROS:
        model.addAttribute("usuario", usuario);
        return "gestion"; // Nombre de tu archivo HTML
    }
}