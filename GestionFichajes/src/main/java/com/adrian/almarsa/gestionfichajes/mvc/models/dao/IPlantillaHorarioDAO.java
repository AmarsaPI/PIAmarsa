package com.adrian.almarsa.gestionfichajes.mvc.models.dao;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.PlantillaHorario;

// Repositorio para la gestión de los turnos y horarios asignados
public interface IPlantillaHorarioDAO extends CrudRepository<PlantillaHorario, Long> {
	
    // Recupera la lista de turnos (días y horas) asociados a un empleado concreto
    // Utilizamos Empleado_Id para mapear directamente al ID de la entidad relacionada
    List<PlantillaHorario> findByEmpleado_Id(Long empleadoId);

}