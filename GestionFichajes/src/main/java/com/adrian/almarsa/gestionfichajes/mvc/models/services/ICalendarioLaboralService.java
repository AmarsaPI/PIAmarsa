package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.util.List;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.CalendarioLaboral;

/**
 * Servicio para la gestión de calendarios laborales.
 */
public interface ICalendarioLaboralService {

    /**
     * Obtiene todos los calendarios laborales.
     * @return lista de calendarios
     */
    List<CalendarioLaboral> findAll();

    /**
     * Guarda o actualiza un calendario laboral.
     * @return calendario guardado
     */
    CalendarioLaboral save(CalendarioLaboral horario);

    /**
     * Busca un calendario laboral por ID.
     * @return calendario encontrado o null
     */
    CalendarioLaboral findById(Long id);

    /**
     * Elimina un calendario laboral por ID.
     */
    void delete(Long id);

    /**
     * Obtiene el calendario laboral asignado a un empleado.
     * @return calendario del empleado o null
     */
    CalendarioLaboral findByEmpleado(Long empleadoId);
}

