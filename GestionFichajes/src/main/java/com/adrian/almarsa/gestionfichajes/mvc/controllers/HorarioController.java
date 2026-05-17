package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.adrian.almarsa.gestionfichajes.mvc.models.dto.EventoCalendarioDTO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Horario;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.PlantillaHorario;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IEmpleadoService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IHorarioService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IPlantillaHorarioService;

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
	
	//Para leer correctamente el json
	@Autowired
	private ObjectMapper objectMapper;
	
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
	
    @DeleteMapping("/horarios-reales/empleado/{empleadoId}/rango")
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
}
