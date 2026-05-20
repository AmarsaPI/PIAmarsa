package com.adrian.almarsa.gestionfichajes.mvc.models.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query; // 👈 ¡ASEGÚRATE DE AÑADIR ESTA IMPORTACIÓN!
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Contrato;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;

public interface IContratoDAO extends CrudRepository<Contrato, Long> {
	
	@Query("SELECT c FROM Contrato c WHERE c.empleado = :empleado " +
           "AND c.fechaInicio <= :fecha " +
           "AND (c.fechaFin IS NULL OR c.fechaFin >= :fecha)")
	Optional<Contrato> findContratoActivoEnFecha(@Param("empleado") Empleado empleado, @Param("fecha") LocalDate fecha);
	
	List<Contrato> findByEmpleado(Empleado empleado);
}