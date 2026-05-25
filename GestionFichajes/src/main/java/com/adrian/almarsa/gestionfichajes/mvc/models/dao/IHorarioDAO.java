package com.adrian.almarsa.gestionfichajes.mvc.models.dao;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Horario;

// Repositorio para la gestión de los turnos y horarios asignados
public interface IHorarioDAO extends CrudRepository<Horario, Long> {
	
    List<Horario> findByEmpleado_Id(Long empleadoId);
    
    List<Horario> findByFechaBetween(LocalDate inicio, LocalDate fin);
}
