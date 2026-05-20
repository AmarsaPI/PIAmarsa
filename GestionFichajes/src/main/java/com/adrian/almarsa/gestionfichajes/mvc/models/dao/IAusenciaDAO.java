package com.adrian.almarsa.gestionfichajes.mvc.models.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Ausencia;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.EstadoAusencia;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.TipoAusencia;

public interface IAusenciaDAO extends CrudRepository<Ausencia, Long> {
	List<Ausencia> findByEmpleado(Empleado empleado);
	

    List<Ausencia> findByEmpleadoIdAndTipo(Long empleadoId, TipoAusencia tipo);
    
    List<Ausencia> findByEstado(EstadoAusencia estado);

    // Verifica si el Empleado tiene una ausencia activa en una fecha concreta
    @Query("SELECT a FROM Ausencia a WHERE a.empleado = :empleado " +
           "AND :fecha BETWEEN a.fechaInicio AND a.fechaFin")
    List<Ausencia> findAusenciasEnFecha(@Param("empleado") Empleado empleado, @Param("fecha") LocalDate fecha);
    
    List<Ausencia> findByEmpleadoIdAndTipoAndFechaInicioBetween(
            Long empleadoId, 
            TipoAusencia tipo, 
            LocalDate desde, 
            LocalDate hasta
        );
    
    @Modifying
    void deleteByEmpleadoIdAndTipoAndEstado(Long empleadoId, TipoAusencia tipo, EstadoAusencia estado);
}
