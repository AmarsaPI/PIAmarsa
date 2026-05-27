package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Horario;

/**
 * Servicio para la gestión de horarios laborales.
 */
public interface IHorarioService {

    /**
     * Obtiene todos los horarios del sistema.
     * @return lista de horarios
     */
    List<Horario> findAll();

    /**
     * Guarda o actualiza un horario.
     * @return horario guardado
     */
    Horario save(Horario horario);

    /**
     * Busca un horario por ID.
     * @return horario encontrado o null
     */
    Horario findById(Long id);

    /**
     * Elimina un horario por ID.
     */
    void delete(Long id);

    /**
     * Obtiene los horarios de un empleado.
     * @return lista de horarios
     */
    List<Horario> findByEmpleado(Long empleadoId);

    /**
     * Obtiene horarios dentro de un rango de fechas.
     * @return lista de horarios
     */
    List<Horario> findByFechaBetween(LocalDate inicio, LocalDate fin);

    /**
     * Obtiene un horario por empleado y fecha.
     * @return horario encontrado o null
     */
    Horario findByEmpleadoIdAndFecha(Long empleadoId, LocalDate fecha);

    /**
     * Actualiza o crea un turno programado para un empleado.
     */
    void actualizarTurnoProgramado(Long empleadoId, LocalDate fecha,
                                   LocalTime nuevaEntrada, LocalTime nuevaSalida,
                                   LocalTime nuevaEntrada2, LocalTime nuevaSalida2);
}
