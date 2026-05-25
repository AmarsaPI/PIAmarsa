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
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.PlantillaHorario;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.SolicitudCambio;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IBolsaHorasService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IEmpleadoService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IFichajeService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IHorarioService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IPlantillaHorarioService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.ISolicitudCambioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class EmpleadoController {

    @Autowired 
    private IEmpleadoService empleadoService;
    
    @Autowired 
    private IHorarioService horarioService;
    
    @Autowired 
    private IBolsaHorasService bolsaHorasService;

    // Página de inicio tras el login
    //HttpSession crea en memoria una sesión que le da al servidor una cookie llamada JSESSIONID(parecido a un token)
    //Así sabremos siempre el usuario que está logueado, otra forma seria hacerlo con un método en sucrityFilterChain
    //llamado processLogin, así no depende del id ni se pasa la pass por la url(más seguro).
    @Autowired private IFichajeService fichajeService; 
    @Autowired private ISolicitudCambioService solicitudService;

    @GetMapping("/index")
    public String mostrarIndex(HttpSession session, Model model) {
        Long id = (Long) session.getAttribute("usuarioLogueadoId");
        if (id == null) return "redirect:/login";

        Empleado usuario = empleadoService.findById(id);
        Fichaje ultimo = fichajeService.findUltimoSinCerrar(id);
        List<Fichaje> listaOlvidos = fichajeService.findFichajesConOlvido(id);

        // --- NUEVA LÓGICA PARA EL HORARIO DE HOY ---
        LocalDate hoy = LocalDate.now();
        // Suponiendo que tienes un método findByEmpleadoAndFecha en tu IHorarioService
        Horario horarioHoy = horarioService.findByEmpleadoIdAndFecha(id, hoy);
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("ultimoFichaje", ultimo);
        model.addAttribute("enJornada", ultimo != null);
        model.addAttribute("listaOlvidos", listaOlvidos);
        model.addAttribute("horarioActual", horarioHoy);
        System.out.println("DEBUG: Horario encontrado para hoy: " + horarioHoy);// <--- ESTO ES LO QUE TE FALTABA

        // Lógica de notificaciones (lo que ya tenías)
        if ("ADMINISTRADOR".equals(usuario.getRol().name())) {
            List<SolicitudCambio> pendientes = solicitudService.findPendientes();
            model.addAttribute("solicitudesPendientes", pendientes);
        }

        return "index";
    }
    // Página específica de horarios personales
    @GetMapping("/horario_personal")
    public String mostrarHorario(HttpSession session, Model model) {
        Long id = (Long) session.getAttribute("usuarioLogueadoId");

        if (id == null) return "redirect:/login";

        // 1. Buscamos al usuario
        Empleado usuario = empleadoService.findById(id);
        
        // 2. Buscamos horarios (los que ya tenías)
        List<Horario> horariosReales = horarioService.findByEmpleado(id);

        // 3. CALCULAMOS LA BOLSA DE HORAS
        double saldo = bolsaHorasService.calcularBolsaAnualAcumulada(usuario);

        // 4. Enviamos todo a la vista
        model.addAttribute("usuario", usuario);
        model.addAttribute("horarios", horariosReales);
        model.addAttribute("saldoTotal", saldo); // <--- Esto es lo que usaremos en el HTML
        model.addAttribute("rangoSemana", "Semana Actual");

        return "horario_personal";
    }
    
    @GetMapping("/gestion_empleados")
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
        return "gestion_empleados"; // Nombre de tu archivo HTML
    }
}