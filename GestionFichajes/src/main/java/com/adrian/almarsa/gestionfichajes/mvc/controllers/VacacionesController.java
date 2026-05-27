package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Ausencia;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.TipoAusencia;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IAusenciaService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IEmpleadoService;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.EstadoAusencia;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador encargado de gestionar todo el flujo de vacaciones y ausencias
 * de los empleados: visualización, solicitud, validación, aprobación,
 * rechazo y representación en el calendario global.
 */
@Controller
public class VacacionesController {

    @Autowired
    private IAusenciaService ausenciaService;
    
    @Autowired
    private IEmpleadoService empleadoService;

    /**
     * Muestra la página de vacaciones del empleado logueado.
     * Calcula los días disponibles, disfrutados y reservados,
     * además de listar todas sus solicitudes.
     *
     * @param model datos enviados a la vista
     * @param session sesión del usuario
     * @return vista de vacaciones o redirección al login si no hay sesión
     */
    @GetMapping("/vacaciones")
    public String verVacaciones(Model model, HttpSession session) {
        Long id = (Long) session.getAttribute("usuarioLogueadoId");
        if (id == null) {
            return "redirect:/login";
        }

        Empleado empleadoLogueado = empleadoService.findById(id);
        if (empleadoLogueado == null) {
            return "redirect:/login";
        }

        List<Ausencia> solicitudes = ausenciaService.obtenerPorEmpleadoYTipo(empleadoLogueado.getId(), TipoAusencia.VACACIONES);
        
        LocalDate hoy = LocalDate.now();
        int diasTotales = 30;

        long diasDisfrutados = solicitudes.stream()
                .filter(a -> a.getEstado() == EstadoAusencia.APROBADA)
                .filter(a -> a.getFechaFin().isBefore(hoy))
                .mapToLong(a -> java.time.temporal.ChronoUnit.DAYS.between(a.getFechaInicio(), a.getFechaFin()) + 1)
                .sum();

        long diasReservados = solicitudes.stream()
                .filter(a -> a.getEstado() == EstadoAusencia.APROBADA)
                .filter(a -> !a.getFechaFin().isBefore(hoy))
                .mapToLong(a -> java.time.temporal.ChronoUnit.DAYS.between(a.getFechaInicio(), a.getFechaFin()) + 1)
                .sum();
        
        long diasDisponibles = diasTotales - diasDisfrutados - diasReservados;

        model.addAttribute("usuario", empleadoLogueado);
        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("diasTotales", diasTotales);
        model.addAttribute("diasDisfrutados", diasDisfrutados);
        model.addAttribute("diasReservados", diasReservados);
        model.addAttribute("diasDisponibles", diasDisponibles);

        return "vacaciones";
    }

    /**
     * Procesa una nueva solicitud de vacaciones.
     * Valida fechas, evita solapamientos con otras solicitudes
     * y registra la ausencia como pendiente.
     *
     * @param inicioStr fecha de inicio en formato texto
     * @param finStr fecha de fin en formato texto
     * @param motivo motivo opcional de la solicitud
     * @param session sesión del usuario
     * @param redirectAttributes mensajes flash para la vista
     * @return redirección a la página de vacaciones
     */
    @PostMapping("/vacaciones/solicitar")
    public String procesarSolicitud(@RequestParam("fechaInicio") String inicioStr,
                                    @RequestParam("fechaFin") String finStr,
                                    @RequestParam(value = "motivo", required = false) String motivo,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        
        Long id = (Long) session.getAttribute("usuarioLogueadoId");
        if (id == null) {
            return "redirect:/login";
        }
        
        Empleado empleadoLogueado = empleadoService.findById(id); 
        if (empleadoLogueado == null) {
            return "redirect:/login";
        }

        LocalDate inicio = LocalDate.parse(inicioStr);
        LocalDate fin = LocalDate.parse(finStr);

        // 1. Validación cronológica básica
        if (inicio.isAfter(fin)) {
            redirectAttributes.addFlashAttribute("mensajeError", "❌ La fecha de inicio no puede ser posterior a la de fin.");
            return "redirect:/vacaciones";
        }

        // Evitar duplicados o solapamiento
        try {
            // Obtenemos todo el histórico de ausencias del empleado que solicita
            List<Ausencia> ausenciasExistentes = ausenciaService.obtenerAusenciasPorEmpleado(empleadoLogueado);

            // Verificamos si la nueva petición choca con días ya reservados
            boolean seSolapa = ausenciasExistentes.stream()
                .filter(a -> a.getEstado() == EstadoAusencia.APROBADA || a.getEstado() == EstadoAusencia.PENDIENTE)
                .anyMatch(a -> {
                    // Lógica de colisión de fechas: (InicioA <= FinB) && (FinA >= InicioB)
                    return !inicio.isAfter(a.getFechaFin()) && !fin.isBefore(a.getFechaInicio());
                });

            if (seSolapa) {
                redirectAttributes.addFlashAttribute("mensajeError", "⚠️ Ya tienes una solicitud Pendiente o Aprobada que se solapa con estas fechas.");
                return "redirect:/vacaciones";
            }

            // 3. Si todo está limpio, guardamos
            Ausencia nuevaAusencia = new Ausencia();
            nuevaAusencia.setEmpleado(empleadoLogueado);
            nuevaAusencia.setFechaInicio(inicio);
            nuevaAusencia.setFechaFin(fin);
            nuevaAusencia.setTipo(TipoAusencia.VACACIONES);
            nuevaAusencia.setObservaciones(motivo);
            nuevaAusencia.setEstado(EstadoAusencia.PENDIENTE);

            ausenciaService.registrarAusencia(nuevaAusencia);
            redirectAttributes.addFlashAttribute("mensajeExito", "🚀 Solicitud de vacaciones enviada correctamente.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "💥 Error al procesar la solicitud: " + e.getMessage());
        }

        return "redirect:/vacaciones";
    }
    
    /**
     * Elimina todas las solicitudes de vacaciones rechazadas
     * del empleado logueado para mantener su historial limpio.
     *
     * @param session sesión del usuario
     * @param redirectAttributes mensajes flash
     * @return redirección a la página de vacaciones
     */
    @PostMapping("/vacaciones/limpiar-rechazadas")
    public String limpiarSolicitudesRechazadas(HttpSession session, RedirectAttributes redirectAttributes) {
        Long id = (Long) session.getAttribute("usuarioLogueadoId");
        if (id == null) {
            return "redirect:/login";
        }

        try {
            ausenciaService.borrarRechazadasPorEmpleadoYTipo(id, TipoAusencia.VACACIONES);
            redirectAttributes.addFlashAttribute("mensajeExito", "🧹 Historial de solicitudes rechazadas limpiado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "💥 Error al limpiar el historial: " + e.getMessage());
        }

        return "redirect:/vacaciones";
    }
    
    /**
     * Muestra el calendario global de ausencias del mes seleccionado.
     * Incluye colores por tipo de ausencia, solicitudes pendientes
     * y navegación entre meses.
     *
     * @param mes mes a visualizar (si no se envía, se usa el actual)
     * @param model datos enviados a la vista
     * @param session sesión del usuario
     * @return vista del calendario global
     */
    @GetMapping("/vacaciones/calendario-global")
    public String verCalendarioGlobal(@RequestParam(value = "mes", required = false) Integer mes,
                                     Model model, HttpSession session) {
        Long idLogueado = (Long) session.getAttribute("usuarioLogueadoId");
        if (idLogueado == null) return "redirect:/login";
        
        Empleado usuarioLogueado = empleadoService.findById(idLogueado);
        model.addAttribute("usuario", usuarioLogueado);
        
        boolean esAdmin = usuarioLogueado.getRol() != null && usuarioLogueado.getRol().name().equals("ADMINISTRADOR");
        model.addAttribute("esAdmin", esAdmin);

        int anioActual = LocalDate.now().getYear();
        model.addAttribute("anio", anioActual);

        // Si no viene mes por la URL, usamos el mes actual del sistema
        if (mes == null) {
            mes = LocalDate.now().getMonthValue();
        }
        model.addAttribute("mesActual", mes);

        // Cálculo del mes anterior y el siguiente para las fechas
        int mesAnterior = (mes == 1) ? 12 : mes - 1;
        int mesSiguiente = (mes == 12) ? 1 : mes + 1;
        model.addAttribute("mesAnterior", mesAnterior);
        model.addAttribute("mesSiguiente", mesSiguiente);

        // Obtener los días totales que tiene el mes seleccionado
        int diasDelMes = LocalDate.of(anioActual, mes, 1).lengthOfMonth();
        model.addAttribute("diasDelMes", diasDelMes);

        // Lista manual de nombres para pintar el título principal
        List<String> nombresMeses = new ArrayList<>();
        nombresMeses.add("ENERO"); nombresMeses.add("FEBRERO"); nombresMeses.add("MARZO");
        nombresMeses.add("ABRIL"); nombresMeses.add("MAYO"); nombresMeses.add("JUNIO");
        nombresMeses.add("JULIO"); nombresMeses.add("AGOSTO"); nombresMeses.add("SEPTIEMBRE");
        nombresMeses.add("OCTUBRE"); nombresMeses.add("NOVIEMBRE"); nombresMeses.add("DICIEMBRE");
        
        model.addAttribute("nombreMes", nombresMeses.get(mes - 1));

        // Carga de la plantilla completa
        List<Empleado> plantilla = empleadoService.findAll();
        model.addAttribute("plantilla", plantilla);

        // Estructuras de mapas para las celdas
        Map<Long, Map<Integer, String>> mapaVacaciones = new HashMap<>();
        Map<Long, Map<Integer, Long>> mapaIds = new HashMap<>();
        
        List<Ausencia> solicitudesPendientes = new ArrayList<>();

        for (Empleado emp : plantilla) {
            mapaVacaciones.put(emp.getId(), new HashMap<>());
            mapaIds.put(emp.getId(), new HashMap<>());
        }

        for (Empleado emp : plantilla) {
        	List<Ausencia> ausencias = ausenciaService.obtenerAusenciasPorEmpleado(emp); // Asegúrate de tener este método en tu servicio

        	for (Ausencia aus : ausencias) {
        	    if (esAdmin && aus.getEstado() == EstadoAusencia.PENDIENTE && !solicitudesPendientes.contains(aus)) {
        	        solicitudesPendientes.add(aus);
        	    }
        	    

        	    LocalDate curr = aus.getFechaInicio();
        	    while (!curr.isAfter(aus.getFechaFin())) {
        	        if (curr.getYear() == anioActual && curr.getMonthValue() == mes) {
        	            int dia = curr.getDayOfMonth();
        	            
        	            String claseColor = "dia-pendiente"; // Azul para pendientes
        	            
        	            if (aus.getEstado() == EstadoAusencia.RECHAZADA) {
        	                claseColor = "dia-rechazado";       // Gray / Trazabilidad
        	            } else if (aus.getEstado() == EstadoAusencia.APROBADA) {
        	                if (aus.getTipo() == TipoAusencia.VACACIONES) {
        	                    claseColor = "dia-aprobado";      // Verde
        	                } else if (aus.getTipo() == TipoAusencia.BAJA_MEDICA) {
        	                    claseColor = "dia-baja-medica";   // Amarillo
        	                } else if (aus.getTipo() == TipoAusencia.PERMISO_RETRIBUIDO) {
        	                    claseColor = "dia-permiso";       // Azul Turquesa
        	                }
        	            }
        	            
        	            mapaVacaciones.get(emp.getId()).put(dia, claseColor);
        	            mapaIds.get(emp.getId()).put(dia, aus.getId());
        	        }
        	        curr = curr.plusDays(1);
        	    }
        	}
        }

        model.addAttribute("mapaVacaciones", mapaVacaciones);
        model.addAttribute("mapaIds", mapaIds);
        model.addAttribute("solicitudesPendientes", solicitudesPendientes);

        return "calendario_global";
    }

    /**
     * Permite al administrador eliminar una solicitud de vacaciones.
     * Tras borrar, redirige al mes donde estaba la ausencia.
     *
     * @param ausenciaId ID de la ausencia a eliminar
     * @param redirectAttributes mensajes flash
     * @return redirección al calendario global
     */
    @PostMapping("/admin/vacaciones/borrar")
    public String borrarSolicitudVacaciones(@RequestParam("ausenciaId") Long ausenciaId,
                                            RedirectAttributes redirectAttributes) {
        try {
            Ausencia ausencia = ausenciaService.buscarPorId(ausenciaId);
            if (ausencia != null) {
                int mesOrigen = ausencia.getFechaInicio().getMonthValue();
                
                // Eliminamos la ausencia de la base de datos
                ausenciaService.eliminarAusencia(ausenciaId);
                
                redirectAttributes.addFlashAttribute("mensajeExito", "🗑️ Vacaciones eliminadas correctamente.");
                
                // Redirigimos al mismo mes donde estábamos
                return "redirect:/vacaciones/calendario-global?mes=" + mesOrigen;
            } else {
                redirectAttributes.addFlashAttribute("mensajeError", "💥 No se encontró la solicitud a borrar.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "💥 Error al borrar: " + e.getMessage());
        }
        
        return "redirect:/vacaciones/calendario-global";
    }
    
    /**
     * Permite al administrador aprobar o rechazar una solicitud de vacaciones.
     * Actualiza el estado y vuelve al calendario global.
     *
     * @param ausenciaId ID de la solicitud
     * @param accion acción elegida (APROBAR o RECHAZAR)
     * @param redirectAttributes mensajes flash
     * @return redirección al calendario global
     */
    @PostMapping("/admin/vacaciones/resolver")
    public String resolverSolicitudVacaciones(@RequestParam("ausenciaId") Long ausenciaId,
                                             @RequestParam("accion") String accion,
                                             RedirectAttributes redirectAttributes) {
        try {
            // 1. Buscamos la solicitud en la base de datos a través del servicio
            Ausencia ausencia = ausenciaService.buscarPorId(ausenciaId);
            
            if (ausencia != null) {
                // 2. Evaluamos qué botón pulsó el administrador en el modal
                if ("APROBAR".equals(accion)) {
                    ausencia.setEstado(EstadoAusencia.APROBADA);
                    redirectAttributes.addFlashAttribute("mensajeExito", "✅ Vacaciones aprobadas correctamente.");
                } else if ("RECHAZAR".equals(accion)) {
                    ausencia.setEstado(EstadoAusencia.RECHAZADA);
                    redirectAttributes.addFlashAttribute("mensajeExito", "❌ Solicitud de vacaciones rechazada.");
                }
                
                // 3. Guardamos los cambios en la base de datos
                ausenciaService.registrarAusencia(ausencia); 
            } else {
                redirectAttributes.addFlashAttribute("mensajeError", "💥 No se encontró la solicitud seleccionada.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "💥 Error al procesar la decisión: " + e.getMessage());
        }

        // 4. Redirigimos de vuelta al Cuadrante Anual
        return "redirect:/vacaciones/calendario-global";
    }
}