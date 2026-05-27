package com.adrian.almarsa.gestionfichajes.mvc.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.CalendarioLaboral;

/**
 * Repositorio encargado de gestionar los registros del calendario laboral.
 * 
 * Permite realizar las operaciones básicas de persistencia y añade un método
 * específico para obtener el calendario asociado a un empleado concreto.
 */
public interface ICalendarioLaboralDAO extends CrudRepository<CalendarioLaboral, Long>{
	
	
	/**
     * Busca el calendario laboral asignado a un empleado mediante su ID.
     * 
     * @param empleadoId identificador del empleado
     * @return el calendario laboral asociado o null si no existe
     */
	CalendarioLaboral findByEmpleados_Id(Long empleadoId);
}
