package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IHorarioDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Horario;

// Servicio que gestiona la lógica de los turnos de trabajo asignados
@Service
public class HorarioServiceImpl implements IHorarioService {

	@Autowired
	private IHorarioDAO horarioDAO;
	
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
	
	// Busca los horarios semanales de un empleado usando la nomenclatura "_" corregida
	@Override
	@Transactional(readOnly = true)
	public List<Horario> findByEmpleado(Long empleadoId) {
	    // Llamamos al método del DAO que navega hasta el ID del objeto Empleado
	    return horarioDAO.findByEmpleado_Id(empleadoId);
	}
}