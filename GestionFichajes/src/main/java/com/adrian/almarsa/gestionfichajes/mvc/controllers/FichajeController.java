package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.EstadoSolicitud;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Fichaje;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.SolicitudCambio;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IEmpleadoService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IFichajeService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.ISolicitudCambioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class FichajeController {

    @Autowired private IEmpleadoService empleadoService;
    @Autowired private IFichajeService fichajeService; 
    @Autowired private ISolicitudCambioService solicitudService; 

    @PostMapping("/fichar/registrar-entrada")
    public String registrarEntrada(HttpSession session) {
        Long id = (Long) session.getAttribute("usuarioLogueadoId");
        Empleado e = empleadoService.findById(id);
        
        Fichaje nuevo = new Fichaje();
        nuevo.setEmpleado(e);
        fichajeService.registrarEntrada(nuevo); 
        
        return "redirect:/index";
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

        return "redirect:/index";
    }
    
    @GetMapping("/historial_fichajes")
    public String mostrarHistorial(
            @RequestParam(required = false) String mes, 
            HttpSession session, Model model) {
        
        Long id = (Long) session.getAttribute("usuarioLogueadoId");
        if (id == null) return "redirect:/login";

        // Si no viene mes, usamos el actual: "2026-05"
        if (mes == null) {
            mes = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        }

        // 1. Buscamos solo los registros de ese mes específico
        List<Fichaje> historialDelMes = fichajeService.findByEmpleadoAndMonth(id, mes);
        
        // 2. Calcular fechas (Mes Anterior / Mes Posterior)
        java.time.YearMonth current = java.time.YearMonth.parse(mes);
        String mesAnterior = current.minusMonths(1).toString();
        String mesPosterior = current.plusMonths(1).toString();

        model.addAttribute("usuario", empleadoService.findById(id));
        model.addAttribute("historial", historialDelMes);
        model.addAttribute("mesActual", mes); // ej: "2026-05"
        model.addAttribute("mesAnterior", mesAnterior);
        model.addAttribute("mesPosterior", mesPosterior);
        
        return "historial_fichajes";
    }
    
    @PostMapping("/solicitar-cambio")
    public String solicitarCambio(
            @RequestParam Long fichajeId,
            @RequestParam String horaEntradaPropuesta,
            @RequestParam String horaSalidaPropuesta,
            @RequestParam String motivo,
            RedirectAttributes flash) {

        // Buscamos el fichaje original
        Fichaje f = fichajeService.findById(fichajeId);
        
        // Creamos la entidad
        SolicitudCambio sol = new SolicitudCambio();
        sol.setFichaje(f);
        sol.setHoraEntradaPropuesta(LocalTime.parse(horaEntradaPropuesta));
        sol.setHoraSalidaPropuesta(LocalTime.parse(horaSalidaPropuesta));
        sol.setEstado(EstadoSolicitud.PENDIENTE);
        sol.setMotivo(motivo);
        
        // Guardamos
        solicitudService.guardar(sol);
        
        flash.addFlashAttribute("mensajeExito", "¡Solicitud enviada correctamente!");
        return "redirect:/historial_fichajes";
    }
    
    @GetMapping("/solicitudes")
    public String listarSolicitudes(Model model, HttpSession session) {
        Long id = (Long) session.getAttribute("usuarioLogueadoId");
        if (id == null) return "redirect:/login";
        
        Empleado usuario = empleadoService.findById(id);
        model.addAttribute("usuario", usuario);

        List<SolicitudCambio> todas = solicitudService.findPendientes();
        
        // DEBUG: Ver qué llega
        System.out.println("TOTAL SOLICITUDES: " + todas.size());
        for(SolicitudCambio s : todas) {
            System.out.println("Solicitud ID: " + s.getId() + " | ¿Tiene Fichaje?: " + (s.getFichaje() != null));
        }

        // Filtramos sin usar .toList() si tu versión de Java es anterior a 16
        List<SolicitudCambio> fichajes = todas.stream()
            .filter(s -> s.getFichaje() != null)
            .collect(Collectors.toList());

        List<SolicitudCambio> turnos = todas.stream()
            .filter(s -> s.getFichaje() == null)
            .collect(Collectors.toList());

        model.addAttribute("fichajes", fichajes);
        model.addAttribute("turnos", turnos);
        
        return "solicitudes";
    }
    
    @PostMapping("/solicitudes/procesar")
    public String procesarSolicitud(
            @RequestParam Long id, 
            @RequestParam String accion, 
            RedirectAttributes flash) {
    	
    	System.out.println(">>> SE HA RECIBIDO UNA PETICIÓN: ID=" + id + " ACCIÓN=" + accion);
        
        try {
            if ("APROBAR".equals(accion)) {
                solicitudService.aprobarSolicitud(id);
                flash.addFlashAttribute("success", "Solicitud aprobada y fichaje actualizado.");
            } else if ("RECHAZAR".equals(accion)) {
                solicitudService.rechazarSolicitud(id);
                flash.addFlashAttribute("success", "Solicitud rechazada correctamente.");
            }
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Ocurrió un error al procesar la solicitud.");
        }
        
        // Redirigimos al listado para que el administrador vea la lista actualizada
        return "redirect:/solicitudes";
    }
    
    @PostMapping("/fichajes/editar")
    public String editarFichajeDirecto(
            @RequestParam Long fichajeId,
            @RequestParam String nuevaEntrada,
            @RequestParam String nuevaSalida,
            HttpSession session) {
        
        // 1. Validar permisos
        Empleado admin = empleadoService.findById((Long) session.getAttribute("usuarioLogueadoId"));
        if (!"ADMINISTRADOR".equals(admin.getRol().name())) {
            return "redirect:/index";
        }

        // 2. Aplicar el cambio directamente
        Fichaje f = fichajeService.findById(fichajeId);
        f.setFechaEntrada(f.getFechaEntrada().with(LocalTime.parse(nuevaEntrada)));
        if(f.getFechaSalida() != null) {
            f.setFechaSalida(f.getFechaSalida().with(LocalTime.parse(nuevaSalida)));
        }
        
        fichajeService.save(f); 
        
        return "redirect:/historial_fichajes";
    }
    
    @GetMapping("/historial_fichajes/pendientes")
    public String listarPendientes(Model model, HttpSession session) {
        Long id = (Long) session.getAttribute("usuarioLogueadoId");
        if (id == null) return "redirect:/login";

        // 1. Obtén solo los fichajes del usuario logueado
        List<Fichaje> historialUsuario = fichajeService.findByEmpleado(id); 
        
        // 2. Filtra los pendientes
        List<Fichaje> pendientes = historialUsuario.stream()
                .filter(f -> f.getFechaSalida() == null)
                .collect(Collectors.toList());
                
        model.addAttribute("usuario", empleadoService.findById(id)); // ¡No olvides pasar el usuario para el nombre en el top-tools!
        model.addAttribute("historial", pendientes);
        
        return "historial_pendientes";
    }
}