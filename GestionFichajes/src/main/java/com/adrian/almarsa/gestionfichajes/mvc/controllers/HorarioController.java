package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.adrian.almarsa.gestionfichajes.mvc.models.dto.EventoCalendarioDTO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Festivo;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Horario;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.PlantillaHorario;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.SolicitudCambio;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IEmpleadoService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IFestivoService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IHorarioService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IPlantillaHorarioService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.ISolicitudCambioService;
import com.lowagie.text.pdf.PdfPCell;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Controller
public class HorarioController {
	
	@Autowired
	private IPlantillaHorarioService plantillaService;
	
	@Autowired
	private IHorarioService horarioService;
	
	@Autowired
	private IEmpleadoService empleadoService;
	
	@Autowired
	private ISolicitudCambioService solicitudCambioService;
	
	//Para leer correctamente el json
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private IFestivoService festivoService;
	
	@PostMapping("/plantillas/guardar")
	public String guardarPlantilla(
	        @RequestParam("nombrePlantilla") String nombre,
	        @RequestParam("datosHorarioJSON") String json) {

	    try {
	        // Convertimos el JSON a nuestra lista de DTOs
	        List<EventoCalendarioDTO> eventosDTO = objectMapper.readValue(json, 
	                new TypeReference<List<EventoCalendarioDTO>>() {});

	        for (EventoCalendarioDTO dto : eventosDTO) {
	            OffsetDateTime odtInicio = OffsetDateTime.parse(dto.getStart());
	            OffsetDateTime odtFin = OffsetDateTime.parse(dto.getEnd());

	            LocalDateTime inicioDT = odtInicio.atZoneSameInstant(java.time.ZoneId.systemDefault()).toLocalDateTime();
	            LocalDateTime finDT = odtFin.atZoneSameInstant(java.time.ZoneId.systemDefault()).toLocalDateTime();

	            PlantillaHorario horario = new PlantillaHorario();
	            horario.setNombrePlantilla(nombre);
	            
	            horario.setDiaSemana(inicioDT.getDayOfWeek()); 
	            horario.setHoraInicio(inicioDT.toLocalTime()); 
	            horario.setHoraFin(finDT.toLocalTime());       

	            plantillaService.save(horario);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        return "redirect:/crear_plantilla?error";
	    }
	    return "redirect:/crear_plantilla?success";
	}
	
	@GetMapping("/crear_plantilla")
	public String nuevaPlantilla(Model model, HttpSession session) {
	    Long empleadoId = (Long) session.getAttribute("usuarioLogueadoId");

	    if (empleadoId == null) {
	        System.out.println("--> [DEBUG] No se encontró el atributo usuarioLogueadoId en la sesión");
	        return "redirect:/login"; 
	    }

	    Empleado empleado = empleadoService.findById(empleadoId);
	    model.addAttribute("usuario", empleado);
	    model.addAttribute("plantilla", new PlantillaHorario());
	    model.addAttribute("empleados", empleadoService.findAll());
	    
	    return "crear_plantilla"; 
	}
	
	@GetMapping("/asignar_horario")
	public String asignarHorario(Model model, HttpSession session) {
	    Long empleadoId = (Long) session.getAttribute("usuarioLogueadoId");

	    if (empleadoId == null) {
	        System.out.println("--> [DEBUG] No se encontró el atributo usuarioLogueadoId en la sesión");
	        return "redirect:/login"; 
	    }

	    Empleado empleado = empleadoService.findById(empleadoId);
	    model.addAttribute("usuario", empleado);
	    model.addAttribute("empleados", empleadoService.findAll());
	    
	    List<PlantillaHorario> todasLasPlantillas = plantillaService.findAll();
	    
	    List<PlantillaHorario> plantillasUnicas = todasLasPlantillas.stream()
	            .collect(Collectors.toMap(
	                    PlantillaHorario::getNombrePlantilla, 
	                    p -> p,                               
	                    (existente, reemplazo) -> existente   
	            ))
	            .values()
	            .stream()
	            .collect(Collectors.toList());
	    
	    model.addAttribute("plantillas", plantillasUnicas);
	    
	    return "asignar_horario"; 
	}
	
	@GetMapping("/gestion_plantillas")
    public String gestionPlantillas(Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioLogueadoId");
        if (usuarioId == null) {
            return "redirect:/login"; 
        }

        Empleado empleado = empleadoService.findById(usuarioId);
        model.addAttribute("usuario", empleado);

        // 1. Recuperamos todas las filas de la tabla de plantillas
        List<PlantillaHorario> todas = plantillaService.findAll();

        // 2. Agrupamos por nombre único para la tabla visual
        List<PlantillaHorario> plantillasAgrupadas = todas.stream()
                .collect(Collectors.toMap(
                        PlantillaHorario::getNombrePlantilla,
                        p -> p,
                        (existente, reemplazo) -> existente
                ))
                .values()
                .stream()
                .collect(Collectors.toList());

        // 3. Pasamos la lista agrupada al modelo
        model.addAttribute("plantillas", plantillasAgrupadas);
        
        model.addAttribute("todasLasPlantillas", todas);

        return "gestion_plantillas"; 
    }
	
    @DeleteMapping("web/horarios-reales/empleado/{empleadoId}/rango")
    public ResponseEntity<?> eliminarRangoFechas(
            @PathVariable Long empleadoId,
            @RequestParam("start") String startStr,
            @RequestParam("end") String endStr) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            // Log de control en la consola de Spring Boot para comprobar qué llega
            System.out.println("====== BORRANDO RANGO ======");
            System.out.println("Empleado: " + empleadoId);
            System.out.println("Fecha Inicio Recibida: " + startStr);
            System.out.println("Fecha Fin Recibida: " + endStr);
            System.out.println("============================");

            // Convertimos los Strings "yyyy-MM-dd" directamente a objetos LocalDate
            java.time.LocalDate start = java.time.LocalDate.parse(startStr);
            java.time.LocalDate end = java.time.LocalDate.parse(endStr);
            
            // 1. Buscamos todos los horarios del empleado seleccionado
            List<Horario> horariosEmpleado = horarioService.findByEmpleado(empleadoId);
            
            // 2. Filtramos los que caen dentro del rango (FullCalendar excluye el día de fin, por eso usamos isBefore)
            List<Horario> horariosAEliminar = horariosEmpleado.stream()
                .filter(h -> (h.getFecha().isEqual(start) || h.getFecha().isAfter(start)) && 
                             (h.getFecha().isBefore(end)))
                .collect(Collectors.toList());
            
            if (horariosAEliminar.isEmpty()) {
                response.put("mensaje", "No se encontraron horarios para eliminar en las fechas seleccionadas.");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            
            // 3. Eliminamos de la base de datos los registros encontrados
            for (Horario h : horariosAEliminar) {
                horarioService.delete(h.getId());
            }
            
            response.put("mensaje", "¡Horarios de la semana eliminados con éxito!");
            response.put("cantidad", horariosAEliminar.size());
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("mensaje", "Error interno al procesar el borrado del rango");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/solicitar-cambio-horario")
    public String procesarSolicitud(@ModelAttribute SolicitudCambio solicitud, 
                                    HttpSession session) {
        
    	Empleado empleado = empleadoService.findById((Long) session.getAttribute("usuarioLogueadoId"));
        solicitud.setEmpleado(empleado); 
        
        solicitudCambioService.guardar(solicitud);
        
        return "redirect:/horario_personal?mensaje=solicitud_enviada";
    }
    
    @GetMapping("/web/horarios-reales/global")
    @ResponseBody
    public ResponseEntity<?> horariosGlobales(
            @RequestParam("start") String start,
            @RequestParam("end") String end) {
        
        try {
            String startLimpio = (start != null && start.length() >= 10) ? start.substring(0, 10) : start;
            String endLimpio = (end != null && end.length() >= 10) ? end.substring(0, 10) : end;

            java.time.LocalDate fechaInicio = java.time.LocalDate.parse(startLimpio);
            java.time.LocalDate fechaFin = java.time.LocalDate.parse(endLimpio);
            
            List<Horario> listaHorarios = horarioService.findAll(); 
            List<Map<String, Object>> eventos = new ArrayList<>(); 

            for (Horario h : listaHorarios) {
                if ((h.getFecha().isEqual(fechaInicio) || h.getFecha().isAfter(fechaInicio)) && 
                    (h.getFecha().isEqual(fechaFin) || h.getFecha().isBefore(fechaFin))) {
                    
                    String nombreEmpleado = (h.getEmpleado() != null) ? h.getEmpleado().getNombre() : "Sin asignar";

                    Map<String, Object> evento = new HashMap<>();
                    evento.put("id", h.getId().toString());
                    evento.put("start", h.getFecha().toString());
                    evento.put("allDay", true);
                    
                    // Formateamos las horas
                    String inicioStr = h.getHoraInicio().toString().substring(0, 5);
                    String finStr = h.getHoraFin().toString().substring(0, 5);
                    String textoTitulo = nombreEmpleado + ": " + inicioStr + " - " + finStr;
                    
                    if (h.getHoraInicio2() != null && h.getHoraFin2() != null) {
                        String inicio2Str = h.getHoraInicio2().toString().substring(0, 5);
                        String fin2Str = h.getHoraFin2().toString().substring(0, 5);
                        textoTitulo += " | " + inicio2Str + " - " + fin2Str;
                    }
                    
                    evento.put("title", textoTitulo); 
                    evento.put("backgroundColor", "#28a745"); 
                    evento.put("textColor", "#ffffff");
                    evento.put("borderColor", "#1e7e34");
                    eventos.add(evento);
                }
            }
            return new ResponseEntity<>(eventos, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/web/festivos/eventos")
    @ResponseBody
    public List<Map<String, Object>> getFestivos(@RequestParam(required = false) Long empleadoId) {
        // Si no hay ID, devolvemos lista vacía para evitar errores
        if (empleadoId == null) {
            return new ArrayList<>(); 
        }
        
        List<Festivo> festivos = festivoService.findByEmpleado(empleadoId);
        
        return festivos.stream().map(f -> {
            Map<String, Object> evento = new HashMap<>();
            evento.put("title", "Festivo: " + f.getDescripcion());
            evento.put("start", f.getFecha().toString());
            evento.put("display", "background");
            evento.put("backgroundColor", "#ffc107");
            evento.put("allDay", true);
            evento.put("groupId", "festivos"); 
            return evento; 
        }).collect(Collectors.toList());
    }
    
    @GetMapping("/web/horarios-reales/empleado/{empleadoId}")
    @ResponseBody
    public ResponseEntity<?> horariosPorEmpleado(
            @PathVariable Long empleadoId,
            @RequestParam("start") String start,
            @RequestParam("end") String end) {
        
        try {
            String startLimpio = (start != null && start.length() >= 10) ? start.substring(0, 10) : start;
            String endLimpio = (end != null && end.length() >= 10) ? end.substring(0, 10) : end;

            java.time.LocalDate fechaInicio = java.time.LocalDate.parse(startLimpio);
            java.time.LocalDate fechaFin = java.time.LocalDate.parse(endLimpio);
            
            List<Horario> listaHorarios = horarioService.findByEmpleado(empleadoId); 
            List<Map<String, Object>> eventos = new ArrayList<>(); 

            for (Horario h : listaHorarios) {
                if ((h.getFecha().isEqual(fechaInicio) || h.getFecha().isAfter(fechaInicio)) && 
                    (h.getFecha().isEqual(fechaFin) || h.getFecha().isBefore(fechaFin))) {
                    
                    Map<String, Object> evento = new HashMap<>();
                    evento.put("id", h.getId().toString());
                    evento.put("start", h.getFecha().toString());
                    evento.put("allDay", true);
                    
                    // Formateamos las horas del primer turno
                    String inicioStr = h.getHoraInicio().toString().substring(0, 5);
                    String finStr = h.getHoraFin().toString().substring(0, 5);
                    String textoTitulo = inicioStr + " - " + finStr;
                    
                    // Si tiene turno de tarde...
                    if (h.getHoraInicio2() != null && h.getHoraFin2() != null) {
                        String inicio2Str = h.getHoraInicio2().toString().substring(0, 5);
                        String fin2Str = h.getHoraFin2().toString().substring(0, 5);
                        textoTitulo += " | " + inicio2Str + " - " + fin2Str;
                    }
                    
                    evento.put("title", textoTitulo); 
                    evento.put("backgroundColor", "#28a745"); 
                    evento.put("textColor", "#ffffff");
                    eventos.add(evento);
                }
            }
            return new ResponseEntity<>(eventos, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/web/plantillas/{id}/preview")
    @ResponseBody
    public ResponseEntity<?> obtenerDetallePlantilla(@PathVariable Long id) {
        PlantillaHorario registroBase = plantillaService.findById(id); 
        
        if (registroBase == null || registroBase.getNombrePlantilla() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        String nombreABuscar = registroBase.getNombrePlantilla();
        
        List<PlantillaHorario> diasDeLaPlantilla = plantillaService.findByNombrePlantilla(nombreABuscar);
        
        List<Map<String, Object>> turnosLimpios = new ArrayList<>();
        for (PlantillaHorario ph : diasDeLaPlantilla) {
            Map<String, Object> turno = new HashMap<>();
            turno.put("diaSemana", ph.getDiaSemana().getValue()); 
            turno.put("horaInicio", ph.getHoraInicio().toString()); 
            turno.put("horaFin", ph.getHoraFin().toString()); 
            
            turnosLimpios.add(turno);
        }
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("nombrePlantilla", nombreABuscar);
        respuesta.put("turnos", turnosLimpios); 
        
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }
   
    @PostMapping("web/horarios-reales")
    @ResponseBody
    public ResponseEntity<?> guardar(@RequestBody Horario horario) {
        try {
            // 1. Verificamos si ya existe un horario para este empleado en esta fecha concreta
            if (horario.getEmpleado() != null && horario.getEmpleado().getId() != null && horario.getFecha() != null) {
                
                // Buscamos todos los horarios de ese empleado
                List<Horario> horariosExistentes = horarioService.findByEmpleado(horario.getEmpleado().getId());
                
                // Filtramos para ver si alguno coincide exactamente con la fecha que intentamos guardar
                java.util.Optional<Horario> horarioDuplicado = horariosExistentes.stream()
                    .filter(h -> h.getFecha().isEqual(horario.getFecha()))
                    .findFirst();
                
                // Al tener el mismo ID, Crudrepository ejecutará un UPDATE en la base de datos reemplazando las horas antiguas.
                if (horarioDuplicado.isPresent()) {
                    horario.setId(horarioDuplicado.get().getId());
                }
            }

            // 2. Guardamos (insertará si el ID es nuevo, o actualizará si el ID ya existía)
            horarioService.save(horario);
            
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Horario procesado con éxito");
            respuesta.put("idGenerado", horario.getId());
            
            return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
            
        } catch (Exception e) {
            e.printStackTrace(); 
            
            Map<String, Object> error = new HashMap<>();
            error.put("mensaje", "Error al procesar el horario");
            error.put("detalles", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("web/horarios-reales/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (horarioService.findById(id) == null) {
                response.put("mensaje", "El horario real ID: " + id + " no existe");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            horarioService.delete(id);
            response.put("mensaje", "Horario real eliminado con éxito");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar el horario de la base de datos");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/web/horarios-reales/mis-turnos")
    @ResponseBody
    public ResponseEntity<?> misTurnos(
            HttpSession session,
            @RequestParam(required = false) String start, // Añade estos parámetros
            @RequestParam(required = false) String end) { 
        
        Long empleadoId = (Long) session.getAttribute("usuarioLogueadoId");
        
        if (empleadoId == null) {
            return new ResponseEntity<>(Map.of("mensaje", "No hay sesión"), HttpStatus.UNAUTHORIZED);
        }

        // Buscamos los horarios del empleado
        List<Horario> listaHorarios = horarioService.findByEmpleado(empleadoId);
        List<Map<String, Object>> eventos = new java.util.ArrayList<>(); 

        for (Horario h : listaHorarios) {
            // Opcional: Si quieres filtrar por rango de fechas recibido (start/end)
            // puedes añadir un if aquí similar al que tienes en 'horariosGlobales'
            
            Map<String, Object> turno1 = new HashMap<>();
            turno1.put("start", h.getFecha().toString());
            turno1.put("title", h.getHoraInicio().toString().substring(0, 5) + " - " + h.getHoraFin().toString().substring(0, 5));
            turno1.put("backgroundColor", "#d1ecf1");
            turno1.put("textColor", "#0c5460");
            
            // Es vital que el formato sea estricto para FullCalendar
            Map<String, Object> props1 = new HashMap<>();
            props1.put("textoPersonalizado", h.getHoraInicio().toString().substring(0, 5) + " a " + h.getHoraFin().toString().substring(0, 5));
            turno1.put("extendedProps", props1);
            
            eventos.add(turno1);

            if (h.getHoraInicio2() != null) { 
                Map<String, Object> turno2 = new HashMap<>();
                turno2.put("start", h.getFecha().toString());
                turno2.put("title", h.getHoraInicio2().toString().substring(0, 5) + " - " + h.getHoraFin2().toString().substring(0, 5));
                turno2.put("backgroundColor", "#fff3cd");
                turno2.put("textColor", "#856404");
                
                Map<String, Object> props2 = new HashMap<>();
                props2.put("textoPersonalizado", h.getHoraInicio2().toString().substring(0, 5) + " a " + h.getHoraFin2().toString().substring(0, 5));
                turno2.put("extendedProps", props2);
                
                eventos.add(turno2);
            }
        }
        return new ResponseEntity<>(eventos, HttpStatus.OK);
    }
    
    @GetMapping("/web/horarios-reales/mis-festivos")
    @ResponseBody
    public ResponseEntity<?> misFestivos(HttpSession session) {
        Long empleadoId = (Long) session.getAttribute("usuarioLogueadoId");
        
        List<Festivo> festivos = festivoService.findByEmpleado(empleadoId);
        
        List<Map<String, Object>> eventos = new ArrayList<>();
        for (Festivo f : festivos) {
            Map<String, Object> festivo = new HashMap<>();
            festivo.put("start", f.getFecha().toString());
            festivo.put("title", f.getDescripcion());
            festivo.put("display", "background");
            festivo.put("backgroundColor", "#ffcccc"); 
            festivo.put("editable", false); 
            eventos.add(festivo);
        }
        return new ResponseEntity<>(eventos, HttpStatus.OK);
    }
    
    @GetMapping("/horarios/pdf/descargar")
    public void exportarPdf(HttpServletResponse response, HttpSession session) throws Exception {
        Long empId = (Long) session.getAttribute("usuarioLogueadoId");
        List<Horario> horarios = horarioService.findByEmpleado(empId);

        // 1. Ordenar los horarios por fecha (para que salgan en orden)
        horarios = horarios.stream()
            .sorted((h1, h2) -> h1.getFecha().compareTo(h2.getFecha()))
            .collect(Collectors.toList());

        // 2. Configurar la respuesta
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=mi_horario.pdf");

        // 3. Crear el documento
        com.lowagie.text.Document document = new com.lowagie.text.Document();
        com.lowagie.text.pdf.PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

     // Título
     document.add(new com.lowagie.text.Paragraph("Mi Horario Semanal"));
     document.add(new com.lowagie.text.Paragraph(" ")); 

     com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(7);
     table.setWidthPercentage(100);

     // 1. Encabezados
     String[] dias = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
     for (String dia : dias) {
         PdfPCell cell = new PdfPCell(new com.lowagie.text.Paragraph(dia));
         cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
         cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
         table.addCell(cell);
     }

     // 2. Lógica para rellenar datos CON fecha y hora
     if (!horarios.isEmpty()) {
         // Calculamos el espacio en blanco inicial (si no empieza en lunes)
         int diaSemanaInicio = horarios.get(0).getFecha().getDayOfWeek().getValue();
         for (int i = 1; i < diaSemanaInicio; i++) {
             table.addCell("-");
         }

         // Rellenar con los horarios
         for (Horario h : horarios) {
             // Formateamos la fecha (ej: 24/05) y el horario
             String fechaStr = h.getFecha().getDayOfMonth() + "/" + h.getFecha().getMonthValue();
             String horarioStr = h.getHoraInicio().toString().substring(0, 5) + "-" + 
                                h.getHoraFin().toString().substring(0, 5);
             
             // Creamos una celda que tenga ambos datos
             PdfPCell cell = new PdfPCell(
                 new com.lowagie.text.Paragraph(fechaStr + "\n" + horarioStr)
             );
             cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
             table.addCell(cell);
         }
     }

     document.add(table);
     document.close();
    }
    
    @GetMapping("/horarios/pdf/descargar-equipo")
    public void exportarPdfCuadrante(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=Cuadrante_Mensual_Vertical.pdf");

        com.lowagie.text.Document document = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4);
        com.lowagie.text.pdf.PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        LocalDate inicioMes = LocalDate.now().with(java.time.temporal.TemporalAdjusters.firstDayOfMonth());
        LocalDate finMes = inicioMes.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth());

        // 1. Creamos un array con los nombres de los meses en español
        String[] mesesEsp = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", 
                             "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

        // 2. Obtenemos el índice del mes (get個月Value() devuelve 1 para Enero, 12 para Diciembre)
        int indiceMes = inicioMes.getMonthValue() - 1; 
        String nombreMes = mesesEsp[indiceMes];

        // 3. Añadimos el título al documento
        document.add(new com.lowagie.text.Paragraph("Cuadrante: " + nombreMes + " " + inicioMes.getYear()));
        document.add(new com.lowagie.text.Paragraph(" "));


        // 2. Iterar por semanas (cada semana es un bloque)
        LocalDate lunes = inicioMes.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        
        while (lunes.isBefore(finMes.plusDays(7))) {
        	String nombreMesSemana = mesesEsp[lunes.getMonthValue() - 1];
        	document.add(new com.lowagie.text.Paragraph("Semana del " + lunes.getDayOfMonth() + " de " + nombreMesSemana));
            
            com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(8);
            table.setWidthPercentage(100);
            
            // Encabezados
            String[] cabeceras = {"Empleado", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
            for (String h : cabeceras) {
                com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Paragraph(h));
                cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                table.addCell(cell);
            }

            List<Empleado> empleados = StreamSupport.stream(empleadoService.findAll().spliterator(), false)
                                                    .collect(Collectors.toList());

            for (Empleado emp : empleados) {
                table.addCell(emp.getNombre());
                for (int i = 0; i < 7; i++) {
                    LocalDate fechaDia = lunes.plusDays(i);
                    Horario h = horarioService.findByEmpleadoIdAndFecha(emp.getId(), fechaDia);
                    
                    String texto = (h != null) ? h.getHoraInicio().toString().substring(0, 5) : "-";
                    table.addCell(new com.lowagie.text.Paragraph(texto));
                }
            }
            document.add(table);
            document.add(new com.lowagie.text.Paragraph(" ")); // Espacio entre semanas
            
            lunes = lunes.plusWeeks(1); // Saltar a la siguiente semana
        }

        document.close();
    }
}
