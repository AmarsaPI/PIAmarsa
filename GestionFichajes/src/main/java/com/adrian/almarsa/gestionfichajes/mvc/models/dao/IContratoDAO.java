package com.adrian.almarsa.gestionfichajes.mvc.models.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query; // 👈 ¡ASEGÚRATE DE AÑADIR ESTA IMPORTACIÓN!
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Contrato;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;

/**
 * Repositorio encargado de gestionar los contratos de los empleados.
 *
 * Permite realizar las operaciones básicas de persistencia y añade
 * consultas específicas para obtener el contrato activo en una fecha
 * concreta o listar todos los contratos asociados a un empleado.
 */
public interface IContratoDAO extends CrudRepository<Contrato, Long> {
	
	/**
     * Busca el contrato activo de un empleado en una fecha determinada.
     * Un contrato se considera activo si:
     * - Su fecha de inicio es anterior o igual a la fecha consultada.
     * - Su fecha de fin es nula o posterior a la fecha consultada.
     *
     * @param empleado empleado del que se quiere obtener el contrato
     * @param fecha fecha a comprobar
     * @return Optional con el contrato activo o vacío si no existe
     */
	@Query("SELECT c FROM Contrato c WHERE c.empleado = :empleado " +
           "AND c.fechaInicio <= :fecha " +
           "AND (c.fechaFin IS NULL OR c.fechaFin >= :fecha)")
	Optional<Contrato> findContratoActivoEnFecha(@Param("empleado") Empleado empleado, @Param("fecha") LocalDate fecha);
	
	List<Contrato> findByEmpleado(Empleado empleado);
}