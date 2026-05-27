package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Fichaje;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Horario;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.SolicitudCambio;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IBolsaHorasService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IEmpleadoService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IFichajeService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IHorarioService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.ISolicitudCambioService;

import jakarta.servlet.http.HttpSession;

/**
 * Controlador encargado de las vistas principales
 * relacionadas con los empleados.
 */
@Controller
public class EmpleadoController {

    @Autowired 
    private IEmpleadoService empleadoService;
    
    @Autowired 
    private IHorarioService horarioService;
    
    @Autowired 
    private IBolsaHorasService bolsaHorasService;

    @Autowired 
    private IFichajeService fichajeService; 
    
    @Autowired 
    private ISolicitudCambioService solicitudService;

    /**
     * Muestra la página principal después del login.
     * 
     * @param session sesión actual
     * @param model modelo de datos
     * @return vista principal
     */

    // Página principal tras iniciar sesión
    @GetMapping("/index")
    public String mostrarIndex(HttpSession session, Model model) {

        Long id =
                (Long) session.getAttribute("usuarioLogueadoId");

        // Comprueba si el usuario ha iniciado sesión
        if (id == null) {

            return "redirect:/login";
        }

        Empleado usuario = empleadoService.findById(id);

        // Busca el último fichaje sin cerrar
        Fichaje ultimo =
                fichajeService.findUltimoSinCerrar(id);

        // Busca fichajes con olvidos
        List<Fichaje> listaOlvidos =
                fichajeService.findFichajesConOlvido(id);

        // Obtiene el horario del día actual
        LocalDate hoy = LocalDate.now();

        Horario horarioHoy =
                horarioService.findByEmpleadoIdAndFecha(id, hoy);
        
        model.addAttribute("usuario", usuario);

        model.addAttribute("ultimoFichaje", ultimo);

        model.addAttribute("enJornada", ultimo != null);

        model.addAttribute("listaOlvidos", listaOlvidos);

        model.addAttribute("horarioActual", horarioHoy);

        // Mensaje de prueba para comprobar el horario
        System.out.println(
                "DEBUG: Horario encontrado para hoy: "
                        + horarioHoy
        );

        // Si es administrador carga solicitudes pendientes
        if ("ADMINISTRADOR".equals(usuario.getRol().name())) {

            List<SolicitudCambio> pendientes =
                    solicitudService.findPendientes();

            model.addAttribute(
                    "solicitudesPendientes",
                    pendientes
            );
        }

        return "index";
    }

    /**
     * Muestra el horario personal del empleado.
     * 
     * @param session sesión actual
     * @param model modelo de datos
     * @return vista del horario
     */

    // Página con el horario personal del empleado
    @GetMapping("/horario_personal")
    public String mostrarHorario(HttpSession session, Model model) {

        Long id =
                (Long) session.getAttribute("usuarioLogueadoId");

        // Comprueba si el usuario ha iniciado sesión
        if (id == null) {

            return "redirect:/login";
        }

        // Obtiene el empleado
        Empleado usuario = empleadoService.findById(id);
        
        // Obtiene los horarios del empleado
        List<Horario> horariosReales =
                horarioService.findByEmpleado(id);

        // Calcula el saldo total de horas
        double saldo =
                bolsaHorasService.calcularBolsaAnualAcumulada(usuario);

        model.addAttribute("usuario", usuario);

        model.addAttribute("horarios", horariosReales);

        model.addAttribute("saldoTotal", saldo);

        model.addAttribute("rangoSemana", "Semana Actual");

        return "horario_personal";
    }
    
    /**
     * Muestra la vista de gestión de empleados.
     * Solo accesible para administradores.
     * 
     * @param session sesión actual
     * @param model modelo de datos
     * @return vista de gestión o redirección
     */

    // Página de gestión de empleados
    @GetMapping("/gestion_empleados")
    public String mostrarGestion(HttpSession session, Model model) {

        Long id =
                (Long) session.getAttribute("usuarioLogueadoId");
        
        // Comprueba si el usuario está autenticado
        if (id == null) {

            return "redirect:/login";
        }

        // Busca el usuario en la base de datos
        Empleado usuario = empleadoService.findById(id);

        // Comprueba si el usuario es administrador
        if (usuario == null
                || !usuario.getRol().name().equals("ADMINISTRADOR")) {

            return "redirect:/index?error=no_autorizado";
        }

        model.addAttribute("usuario", usuario);

        return "gestion_empleados";
    }
}