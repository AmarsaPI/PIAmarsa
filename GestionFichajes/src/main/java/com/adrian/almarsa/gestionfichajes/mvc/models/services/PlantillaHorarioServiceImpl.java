package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IPlantillaHorarioDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.PlantillaHorario;

// Servicio que gestiona la lógica de los turnos de trabajo asignados
@Service
public class PlantillaHorarioServiceImpl implements IPlantillaHorarioService {

	@Autowired
	private IPlantillaHorarioDAO horarioDAO;
	
	// Obtiene todos los horarios del sistema (útil para administración)
	@Override
	@Transactional(readOnly = true)
	public List<PlantillaHorario> findAll() {
		return (List<PlantillaHorario>) horarioDAO.findAll();
	}
	
	// Registra o edita un horario. Incluye una validación básica de integridad
	@Override
	@Transactional
	public PlantillaHorario save(PlantillaHorario horario) {
		if(horario.getHoraInicio() == null || horario.getHoraFin() == null) {
		    throw new RuntimeException("Las horas del horario no pueden ser nulas");
		}
		return horarioDAO.save(horario);
	}
	
	// Recupera un horario individual por su ID
	@Override
	@Transactional(readOnly = true) 
	public PlantillaHorario findById(Long id) {
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
	public List<PlantillaHorario> findByNombrePlantilla(String nombrePlantilla) {
	    return horarioDAO.findByNombrePlantilla(nombrePlantilla);
	}
}