package com.adrian.almarsa.gestionfichajes.mvc.models.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Fichaje;

// Repositorio para gestionar la persistencia de los registros de entrada y salida
public interface IFichajeDAO extends CrudRepository<Fichaje, Long> {
	
    // Busca un fichaje abierto (sin fecha de salida) para un empleado específico
    // Se usa para validar que un empleado no fiche entrada dos veces seguidas
	Optional<Fichaje> findFirstByEmpleadoIdAndFechaSalidaIsNullOrderByIdDesc(Long empleadoId);

    // Obtiene el historial completo de fichajes de un empleado
    // El uso de "_" (Empleado_Id) garantiza que JPA busque por el ID del objeto Empleado
    List<Fichaje> findByEmpleado_Id(Long empleadoId);
    
    @Query("SELECT f FROM Fichaje f WHERE f.empleado.id = :empleadoId AND FUNCTION('DATE', f.fechaEntrada) = :fecha")
    List<Fichaje> findByEmpleadoIdAndFecha(@Param("empleadoId") Long empleadoId, @Param("fecha") LocalDate fecha);
    
    @Query("SELECT f FROM Fichaje f WHERE f.empleado.id = :empleadoId " +
    	       "AND f.fechaSalida IS NULL " +
    	       "AND FUNCTION('DATE', f.fechaEntrada) < CURRENT_DATE")
	List<Fichaje> findFichajesConOlvido(@Param("empleadoId") Long empleadoId);
    
    @Query("SELECT f FROM Fichaje f WHERE f.empleado.id = :empleadoId " +
    	       "AND TO_CHAR(f.fechaEntrada, 'YYYY-MM') = :anioMes " +
    	       "ORDER BY f.fechaEntrada ASC")
	List<Fichaje> findByEmpleadoAndMonth(@Param("empleadoId") Long empleadoId, 
    	                                     @Param("anioMes") String anioMes);
    
    @Query("SELECT f FROM Fichaje f WHERE f.empleado.id = :empleadoId AND YEAR(f.fechaEntrada) = :anio")
    List<Fichaje> findByEmpleadoAndYear(@Param("empleadoId") Long empleadoId, @Param("anio") int anio);
}