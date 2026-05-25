package com.adrian.almarsa.gestionfichajes.mvc.models.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Horario;

// Repositorio para la gestión de los turnos y horarios asignados
public interface IHorarioDAO extends CrudRepository<Horario, Long> {
	
    List<Horario> findByEmpleado_Id(Long empleadoId);
    
    List<Horario> findByFechaBetween(LocalDate inicio, LocalDate fin);
    
    @Modifying
    void deleteByEmpleadoAndFechaBetween(Empleado empleado, LocalDate fechaInicio, LocalDate fechaFin);
    
 // Consulta personalizada para obtener los horarios de un empleado en un año específico
    @Query("SELECT h FROM Horario h WHERE h.empleado.id = :empleadoId AND FUNCTION('YEAR', h.fecha) = :anio")
    List<Horario> findByEmpleadoIdAndYear(@Param("empleadoId") Long empleadoId, @Param("anio") int anio);
    
    Optional<Horario> findByEmpleadoIdAndFecha(Long empleadoId, LocalDate fecha);
}
