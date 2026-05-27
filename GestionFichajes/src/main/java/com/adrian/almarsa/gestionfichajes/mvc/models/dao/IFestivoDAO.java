package com.adrian.almarsa.gestionfichajes.mvc.models.dao;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Festivo;

/**
 * Repositorio encargado de gestionar los días festivos del calendario laboral.
 *
 * Permite realizar las operaciones básicas de persistencia y añade métodos
 * específicos para obtener festivos asociados a un calendario o a un empleado.
 */
public interface IFestivoDAO extends CrudRepository<Festivo, Long> {
    
	/**
     * Obtiene todos los festivos pertenecientes a un calendario concreto.
     *
     * @param calendarioId identificador del calendario laboral
     * @return lista de festivos asociados al calendario
     */
    List<Festivo> findByCalendarioId(Long calendarioId);
    
    /**
     * Obtiene los festivos de un empleado navegando a través de la relación
     * entre calendario laboral y empleados.
     *
     * @param empleadoId identificador del empleado
     * @return lista de festivos que afectan al empleado
     */
    List<Festivo> findByCalendario_Empleados_Id(Long empleadoId);
}