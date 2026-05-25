package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IEmpleadoDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IHorarioDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Horario;

// Servicio que gestiona la lógica de los turnos de trabajo asignados
@Service
public class HorarioServiceImpl implements IHorarioService {

	@Autowired
	private IHorarioDAO horarioDAO;
	
	@Autowired 
	private IAusenciaService ausenciaService;
	
    @Autowired 
    private IFestivoService festivoService;

	@Autowired
	private IEmpleadoDAO empleadoDAO; 
	
	// Obtiene todos los horarios del sistema (útil para administración)
	@Override
	@Transactional(readOnly = true)
	public List<Horario> findAll() {
		return (List<Horario>) horarioDAO.findAll();
	}
	
	// Registra o edita un horario. Incluye una validación básica de integridad
	@Override
	@Transactional
	public Horario save(Horario horario) {
	    Empleado emp = horario.getEmpleado();
	    LocalDate fecha = horario.getFecha();

	    // 1. Validar Bajas / Vacaciones Aprobadas
	    if (ausenciaService.esEmpleadoAusenteAprobado(emp, fecha)) {
	        throw new RuntimeException("❌ No se puede asignar turno: el empleado tiene una BAJA o VACACIÓN APROBADA.");
	    }

	    // 2. Validar Festivos
	    if (isFestivo(fecha, emp.getId())) {
	        throw new RuntimeException("⚠️ No se puede asignar turno: la fecha seleccionada es un FESTIVO.");
	    }

	    if(horario.getHoraInicio() == null || horario.getHoraFin() == null) {
	        throw new RuntimeException("Las horas del horario no pueden ser nulas");
	    }
	    return horarioDAO.save(horario);
	}
	
	// Recupera un horario individual por su ID
	@Override
	@Transactional(readOnly = true) 
	public Horario findById(Long id) {
		return horarioDAO.findById(id).orElse(null);
	}
	
	// Elimina un turno específico de la base de datos
	@Override
	@Transactional
	public void delete(Long id) {
		horarioDAO.deleteById(id);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Horario> findByEmpleado(Long empleadoId) {
	    List<Horario> todos = horarioDAO.findByEmpleado_Id(empleadoId);
	    
	    if (todos.isEmpty()) return todos;

	    return todos.stream()
	        .filter(h -> {
	            Empleado emp = h.getEmpleado();
	            LocalDate fecha = h.getFecha();

	            // 1. Validar si tiene una ausencia APROBADA o una BAJA (bloqueante)
	            // Asumimos que tienes el método esEmpleadoAusenteBloqueante que creamos antes
	            boolean tieneBloqueoPorAusencia = ausenciaService.esEmpleadoAusenteAprobado(emp, fecha);
	            
	            // 2. Validar si es festivo
	            boolean esFestivo = isFestivo(fecha, empleadoId);
	            
	            // Solo devolvemos el horario si NO tiene bloqueos y NO es festivo
	            return !tieneBloqueoPorAusencia && !esFestivo;
	        })
	        .collect(Collectors.toList());
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Horario> findByFechaBetween(LocalDate inicio, LocalDate fin) {
	    return horarioDAO.findByFechaBetween(inicio, fin); 
	}
	
	private boolean isFestivo(LocalDate fecha, Long empleadoId) {
		return festivoService.existeFestivoEnFecha(fecha, empleadoId);
    }

	@Override
	public Horario findByEmpleadoIdAndFecha(Long empleadoId, LocalDate fecha) {
		return horarioDAO.findByEmpleadoIdAndFecha(empleadoId, fecha)
                .orElse(null); 
	}

	@Override
	@Transactional
	public void actualizarTurnoProgramado(Long empleadoId, LocalDate fecha, 
	                                      LocalTime nuevaEntrada, LocalTime nuevaSalida, 
	                                      LocalTime nuevaEntrada2, LocalTime nuevaSalida2) {
	    
	    // 1. Buscamos el horario existente
	    Horario horario = findByEmpleadoIdAndFecha(empleadoId, fecha);
	    
	    if (horario != null) {
	        // Si existe, el empleado ya está asignado, solo actualizamos las horas
	        horario.setHoraInicio(nuevaEntrada);
	        horario.setHoraFin(nuevaSalida);
	        horario.setHoraInicio2(nuevaEntrada2);
	        horario.setHoraFin2(nuevaSalida2);
	        horario.setFecha(fecha);
	        save(horario);
	    } else {
	        Empleado empleado = empleadoDAO.findById(empleadoId)
	                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + empleadoId));
	            
	            if (ausenciaService.esEmpleadoAusenteAprobado(empleado, fecha)) {
	                throw new IllegalStateException("No se puede modificar el turno...");
	            }
	            
	            // CORRECCIÓN: Crea el objeto asegurando la fecha
	            Horario nuevoHorario = new Horario();
	            nuevoHorario.setEmpleado(empleado);
	            nuevoHorario.setFecha(fecha); // <--- ESTO ES LO QUE ESTABA FALLANDO
	            nuevoHorario.setHoraInicio(nuevaEntrada);
	            nuevoHorario.setHoraFin(nuevaSalida);
	            nuevoHorario.setHoraInicio2(nuevaEntrada2);
	            nuevoHorario.setHoraFin2(nuevaSalida2);
	            nuevoHorario.setTipo("LABORABLE");
	            
	            save(nuevoHorario);
	    }
	}
}
