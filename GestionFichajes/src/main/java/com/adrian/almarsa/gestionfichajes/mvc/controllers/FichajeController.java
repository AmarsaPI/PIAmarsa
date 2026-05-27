package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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

/**
 * Controla fichajes (entrada/salida), el historial y las solicitudes de cambio desde la vista.
 */
@Controller
public class FichajeController {

    @Autowired 
    private IEmpleadoService empleadoService;

    @Autowired 
    private IFichajeService fichajeService; 

    @Autowired 
    private ISolicitudCambioService solicitudService; 

    /**
     * Registra la entrada del usuario en sesión y crea un fichaje nuevo.
     *
     * @param session sesión actual
     * @return redirect al index
     */
    @PostMapping("/fichar/registrar-entrada")
    public String registrarEntrada(HttpSession session) {

        Long id =
                (Long) session.getAttribute("usuarioLogueadoId");

        Empleado e =
                empleadoService.findById(id);
        
        // Crear nuevo fichaje
        Fichaje nuevo = new Fichaje();

        nuevo.setEmpleado(e);

        fichajeService.registrarEntrada(nuevo); 
        
        return "redirect:/index";
    }

    /**
     * Marca la salida ahora en el último fichaje abierto del usuario.
     *
     * @param session sesión actual
     * @param model modelo de datos
     * @return redirect al index
     */
    @PostMapping("/fichar/registrar-salida")
    public String registrarSalida(HttpSession session,
                                  Model model) {

        Long id =
                (Long) session.getAttribute("usuarioLogueadoId");
        
        Empleado usuario =
                empleadoService.findById(id);

        // Busca el último fichaje abierto
        Fichaje abierto =
                fichajeService.findUltimoSinCerrar(id);
        
        // Si existe un fichaje abierto lo cierra
        if (abierto != null) {

            abierto.setFechaSalida(LocalDateTime.now());

            fichajeService.save(abierto);

            model.addAttribute(
                    "mensajeExito",
                    "¡Jornada finalizada con éxito!"
            );
        }

        // Actualiza datos de la vista
        model.addAttribute("usuario", usuario);

        model.addAttribute("ultimoFichaje", null);

        model.addAttribute("enJornada", false);

        return "redirect:/index";
    }
    
    /**
     * Muestra el historial de fichajes del usuario por mes (yyyy-MM).
     * Si no se pasa mes, usa el mes actual.
     *
     * @param mes mes seleccionado (opcional)
     * @param session sesión actual
     * @param model modelo de datos
     * @return vista historial_fichajes
     */
    @GetMapping("/historial_fichajes")
    public String mostrarHistorial(
            @RequestParam(required = false) String mes, 
            HttpSession session,
            Model model) {
        
        Long id =
                (Long) session.getAttribute("usuarioLogueadoId");

        // Comprueba si el usuario ha iniciado sesión
        if (id == null) {

            return "redirect:/login";
        }

        // Si no se selecciona mes usa el actual
        if (mes == null) {

            mes = java.time.LocalDate.now().format(
                    java.time.format.DateTimeFormatter.ofPattern(
                            "yyyy-MM"
                    )
            );
        }

        // Obtiene los fichajes del mes seleccionado
        List<Fichaje> historialDelMes =
                fichajeService.findByEmpleadoAndMonth(id, mes);
        
        // Calcula el mes anterior y el siguiente
        java.time.YearMonth current =
                java.time.YearMonth.parse(mes);

        String mesAnterior =
                current.minusMonths(1).toString();

        String mesPosterior =
                current.plusMonths(1).toString();

        model.addAttribute(
                "usuario",
                empleadoService.findById(id)
        );

        model.addAttribute("historial", historialDelMes);

        model.addAttribute("mesActual", mes);

        model.addAttribute("mesAnterior", mesAnterior);

        model.addAttribute("mesPosterior", mesPosterior);
        
        return "historial_fichajes";
    }
    
    /**
     * Crea una solicitud de cambio de horas para un fichaje concreto.
     *
     * @param fichajeId id del fichaje
     * @param horaEntradaPropuesta HH:mm
     * @param horaSalidaPropuesta HH:mm
     * @param motivo motivo del cambio
     * @param flash mensajes temporales
     * @return redirect al historial
     */
    @PostMapping("/solicitar-cambio")
    public String solicitarCambio(
            @RequestParam Long fichajeId,
            @RequestParam String horaEntradaPropuesta,
            @RequestParam String horaSalidaPropuesta,
            @RequestParam String motivo,
            RedirectAttributes flash) {

        // Busca el fichaje original
        Fichaje f =
                fichajeService.findById(fichajeId);
        
        // Crear nueva solicitud
        SolicitudCambio sol = new SolicitudCambio();

        sol.setFichaje(f);

        sol.setHoraEntradaPropuesta(
                LocalTime.parse(horaEntradaPropuesta)
        );

        sol.setHoraSalidaPropuesta(
                LocalTime.parse(horaSalidaPropuesta)
        );

        sol.setEstado(EstadoSolicitud.PENDIENTE);

        sol.setMotivo(motivo);
        
        // Guarda la solicitud
        solicitudService.guardar(sol);
        
        flash.addFlashAttribute(
                "mensajeExito",
                "¡Solicitud enviada correctamente!"
        );

        return "redirect:/historial_fichajes";
    }
    
    /**
     * Lista las solicitudes pendientes y las separa por tipo (fichajes/turnos).
     *
     * @param model modelo de datos
     * @param session sesión actual
     * @return vista solicitudes
     */
    @GetMapping("/solicitudes")
    public String listarSolicitudes(Model model,
                                    HttpSession session) {

        Long id =
                (Long) session.getAttribute("usuarioLogueadoId");

        if (id == null) {

            return "redirect:/login";
        }
        
        Empleado usuario =
                empleadoService.findById(id);

        model.addAttribute("usuario", usuario);

        // Obtiene todas las solicitudes pendientes
        List<SolicitudCambio> todas =
                solicitudService.findPendientes();
        
        // Mensajes de prueba
        System.out.println(
                "TOTAL SOLICITUDES: " + todas.size()
        );

        for (SolicitudCambio s : todas) {

            System.out.println(
                    "Solicitud ID: "
                            + s.getId()
                            + " | ¿Tiene Fichaje?: "
                            + (s.getFichaje() != null)
            );
        }

        // Filtra solicitudes de fichajes
        List<SolicitudCambio> fichajes =
                todas.stream()

                .filter(s -> s.getFichaje() != null)

                .collect(Collectors.toList());

        // Filtra solicitudes de turnos
        List<SolicitudCambio> turnos =
                todas.stream()

                .filter(s -> s.getFichaje() == null)

                .collect(Collectors.toList());

        model.addAttribute("fichajes", fichajes);

        model.addAttribute("turnos", turnos);
        
        return "solicitudes";
    }
    
    /**
     * Procesa una solicitud (aprobar o rechazar).
     *
     * @param id id de la solicitud
     * @param accion "APROBAR" o "RECHAZAR"
     * @param flash mensajes temporales
     * @return redirect a /solicitudes
     */
    @PostMapping("/solicitudes/procesar")
    public String procesarSolicitud(
            @RequestParam Long id, 
            @RequestParam String accion, 
            RedirectAttributes flash) {
    	
    	System.out.println(
                ">>> PETICIÓN RECIBIDA: ID="
                        + id
                        + " ACCIÓN="
                        + accion
        );
        
        try {

            // Aprobar solicitud
            if ("APROBAR".equals(accion)) {

                solicitudService.aprobarSolicitud(id);

                flash.addFlashAttribute(
                        "success",
                        "Solicitud aprobada correctamente."
                );

            // Rechazar solicitud
            } else if ("RECHAZAR".equals(accion)) {

                solicitudService.rechazarSolicitud(id);

                flash.addFlashAttribute(
                        "success",
                        "Solicitud rechazada correctamente."
                );
            }

        } catch (Exception e) {

            flash.addFlashAttribute(
                    "error",
                    "Error al procesar la solicitud."
            );
        }
        
        return "redirect:/solicitudes";
    }
    
    /**
     * Edita directamente las horas de un fichaje (solo administrador).
     *
     * @param fichajeId id del fichaje
     * @param nuevaEntrada HH:mm
     * @param nuevaSalida HH:mm
     * @param session sesión actual
     * @return redirect al historial
     */
    @PostMapping("/fichajes/editar")
    public String editarFichajeDirecto(
            @RequestParam Long fichajeId,
            @RequestParam String nuevaEntrada,
            @RequestParam String nuevaSalida,
            HttpSession session) {
        
        // Comprueba si el usuario es administrador
        Empleado admin =
                empleadoService.findById(
                        (Long) session.getAttribute(
                                "usuarioLogueadoId"
                        )
                );

        if (!"ADMINISTRADOR".equals(admin.getRol().name())) {

            return "redirect:/index";
        }

        // Busca el fichaje
        Fichaje f =
                fichajeService.findById(fichajeId);

        // Cambia la hora de entrada
        f.setFechaEntrada(
                f.getFechaEntrada().with(
                        LocalTime.parse(nuevaEntrada)
                )
        );

        // Cambia la hora de salida
        if (f.getFechaSalida() != null) {

            f.setFechaSalida(
                    f.getFechaSalida().with(
                            LocalTime.parse(nuevaSalida)
                    )
            );
        }
        
        fichajeService.save(f); 
        
        return "redirect:/historial_fichajes";
    }
    
    /**
     * Lista los fichajes del usuario que siguen abiertos (sin salida).
     *
     * @param model modelo de datos
     * @param session sesión actual
     * @return vista historial_pendientes
     */
    @GetMapping("/historial_fichajes/pendientes")
    public String listarPendientes(Model model,
                                   HttpSession session) {

        Long id =
                (Long) session.getAttribute("usuarioLogueadoId");

        if (id == null) {

            return "redirect:/login";
        }

        Empleado usuario =
                empleadoService.findById(id);

        model.addAttribute("usuario", usuario);

        // Obtiene el historial completo
        List<Fichaje> historialUsuario =
                fichajeService.findByEmpleado(id);

        // Filtra los fichajes sin salida
        List<Fichaje> pendientes =
                historialUsuario.stream()

                .filter(f -> f.getFechaSalida() == null)

                .collect(Collectors.toList());
                
        model.addAttribute("historial", pendientes);
        
        return "historial_pendientes";
    }
}
