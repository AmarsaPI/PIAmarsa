package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Festivo;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Horario;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IFestivoService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IHorarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * API REST encargada de gestionar los horarios reales de los empleados.
 * Permite consultarlos, crearlos, actualizarlos y detectar duplicados
 * automáticamente según la fecha y el empleado.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"*"})
public class HorarioRestController {

    @Autowired
    private IHorarioService horarioService;
    
    @Autowired
    private IFestivoService festivoService;

    /**
     * Devuelve el listado completo de horarios registrados.
     * 
     * @return lista con todos los horarios reales almacenados
     */
    @GetMapping("/horarios-reales")
    public List<Horario> index() {
        return horarioService.findAll();
    }

    /**
     * Busca un horario concreto por su ID.
     * 
     * @param id identificador del horario
     * @return el horario encontrado o un mensaje de error si no existe
     */
    @GetMapping("/horarios-reales/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Horario horario = horarioService.findById(id);
            if (horario == null) {
                response.put("mensaje", "El horario real ID: " + id + " no existe");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(horario, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al consultar el horario en la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Guarda un horario nuevo o actualiza uno existente si ya había un registro
     * para el mismo empleado y la misma fecha. Esto evita duplicados y mantiene
     * siempre un único horario por día.
     * 
     * @param horario datos del horario a guardar
     * @return mensaje de confirmación o detalles del error
     */
    @PostMapping("/horarios-reales")
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

    /**
     * Actualiza un horario existente usando su ID.  
     * Si el horario no existe, devuelve un mensaje informándolo.  
     * También valida los datos recibidos antes de aplicar los cambios.
     *
     * @param horario datos actualizados del horario
     * @param result resultado de la validación
     * @param id identificador del horario a modificar
     * @return mensaje de éxito o error según el resultado de la operación
     */
    @PutMapping("/horarios-reales/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Horario horario, BindingResult result, @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            Horario horarioActual = horarioService.findById(id);
            if (horarioActual == null) {
                response.put("mensaje", "El horario real ID: " + id + " no existe");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Actualizamos los campos específicos de la fecha real
            horarioActual.setFecha(horario.getFecha());
            horarioActual.setHoraInicio(horario.getHoraInicio());
            horarioActual.setHoraFin(horario.getHoraFin());
            horarioActual.setEmpleado(horario.getEmpleado());
            // Si añadiste el campo 'tipo' o 'festivo', actualízalo aquí también

            Horario horarioUpdated = horarioService.save(horarioActual);
            response.put("mensaje", "Horario real actualizado con éxito");
            response.put("horario", horarioUpdated);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar el horario en la base de datos");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Elimina un horario por su ID.  
     * Si el horario no existe, devuelve un mensaje indicándolo.
     *
     * @param id identificador del horario a eliminar
     * @return mensaje confirmando el borrado o informando del error
     */
    @DeleteMapping("/horarios-reales/{id}")
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

    /**
     * Devuelve los turnos del empleado actualmente logueado.  
     * La información se adapta al formato esperado por el calendario del frontend,
     * incluyendo fecha, horas y un texto personalizado.
     *
     * @param session sesión del usuario para obtener su ID
     * @param start fecha de inicio opcional para filtrar (no usada actualmente)
     * @param end fecha de fin opcional para filtrar (no usada actualmente)
     * @return lista de eventos con los turnos del empleado
     */
    @GetMapping("/horarios-reales/mis-turnos")
    public ResponseEntity<?> misTurnos(HttpSession session, @RequestParam(required = false) String start, @RequestParam(required = false) String end) {
    	
        Long empleadoId = (Long) session.getAttribute("usuarioLogueadoId");
        if (empleadoId == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        List<Horario> listaHorarios = horarioService.findByEmpleado(empleadoId);
        List<Map<String, Object>> eventos = new ArrayList<>();
        for (Horario h : listaHorarios) {
            Map<String, Object> turno1 = new HashMap<>();
            turno1.put("start", h.getFecha().toString());
            turno1.put("title", h.getHoraInicio().toString().substring(0, 5) + " - " + h.getHoraFin().toString().substring(0, 5));
            turno1.put("extendedProps", Map.of("textoPersonalizado", h.getHoraInicio().toString().substring(0, 5) + " a " + h.getHoraFin().toString().substring(0, 5)));
            eventos.add(turno1);
            
            if (h.getHoraInicio2() != null) {
                Map<String, Object> turno2 = new HashMap<>();
                turno2.put("start", h.getFecha().toString());
                turno2.put("title", h.getHoraInicio2().toString().substring(0, 5) + " - " + h.getHoraFin2().toString().substring(0, 5));
                eventos.add(turno2);
            }
        }
        return new ResponseEntity<>(eventos, HttpStatus.OK);
    }

    /**
     * Devuelve los días festivos del empleado actualmente logueado.
     * La información se adapta al formato del calendario del frontend,
     * mostrando cada festivo como un bloque de fondo.
     *
     * @param session sesión del usuario para obtener su ID
     * @return lista de eventos con los festivos del empleado
     */
    @GetMapping("/horarios-reales/mis-festivos")
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

    /**
     * Obtiene los horarios de un empleado dentro de un rango de fechas.
     * El resultado se devuelve en formato compatible con el calendario,
     * incluyendo horas, colores y turnos partidos si los hubiera.
     *
     * @param empleadoId ID del empleado a consultar
     * @param start fecha de inicio del rango (yyyy-MM-dd)
     * @param end fecha de fin del rango (yyyy-MM-dd)
     * @return lista de eventos del empleado dentro del rango indicado
     */
    @GetMapping("/horarios-reales/empleado/{empleadoId}")
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
                // Filtro de rango de fechas
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
                    
                    // Si tiene turno de tarde, se concatena (Lógica recuperada)
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

    /**
     * Devuelve los horarios de todos los empleados dentro de un rango de fechas.
     * Cada evento incluye el nombre del empleado y sus turnos del día,
     * formateados para mostrarse correctamente en el calendario.
     *
     * @param start fecha de inicio del rango (yyyy-MM-dd)
     * @param end fecha de fin del rango (yyyy-MM-dd)
     * @return lista de eventos globales para el calendario
     */
    @GetMapping("/horarios-reales/global")
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

    /**
     * Elimina todos los horarios de un empleado dentro de un rango de fechas.
     * Se usa normalmente para limpiar una semana completa antes de volver a planificarla.
     *
     * @param empleadoId ID del empleado cuyos horarios se van a borrar
     * @param startStr fecha de inicio del rango (yyyy-MM-dd)
     * @param endStr fecha de fin del rango (yyyy-MM-dd)
     * @return mensaje indicando cuántos horarios fueron eliminados
     */
    @DeleteMapping("/horarios-reales/empleado/{empleadoId}/rango")
    public ResponseEntity<?> eliminarRangoFechas(
            @PathVariable Long empleadoId,
            @RequestParam("start") String startStr,
            @RequestParam("end") String endStr) {
        
        Map<String, Object> response = new HashMap<>();
        try {
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