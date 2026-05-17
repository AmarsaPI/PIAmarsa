package com.adrian.almarsa.gestionfichajes.mvc.models.dao;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.PlantillaHorario;

// Repositorio para la gestión de los turnos y horarios asignados
public interface IPlantillaHorarioDAO extends CrudRepository<PlantillaHorario, Long> {
	
    List<PlantillaHorario> findByNombrePlantilla(String nombrePlantilla);
}