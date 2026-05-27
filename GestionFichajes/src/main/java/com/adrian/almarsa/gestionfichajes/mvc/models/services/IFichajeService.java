package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.time.LocalDate;
import java.util.List;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Fichaje;

/**
 * Servicio para la gestión del ciclo de fichajes.
 */
public interface IFichajeService {

    /**
     * Obtiene todos los fichajes.
     * @return lista de fichajes
     */
    List<Fichaje> findAll();

    /**
     * Busca un fichaje por ID.
     * @return fichaje encontrado o null
     */
    Fichaje findById(Long id);

    /**
     * Elimina un fichaje por ID.
     */
    void delete(Long id);

    /**
     * Guarda o actualiza un fichaje.
     * @return fichaje guardado
     */
    Fichaje save(Fichaje fichaje);

    /**
     * Registra la entrada de un empleado.
     * @return fichaje creado
     */
    Fichaje registrarEntrada(Fichaje fichaje);

    /**
     * Registra la salida de un fichaje.
     * @return fichaje actualizado
     */
    Fichaje registrarSalida(Long fichajeId);

    /**
     * Obtiene los fichajes de un empleado.
     * @return lista de fichajes
     */
    List<Fichaje> findByEmpleado(Long empleadoId);

    /**
     * Obtiene el último fichaje sin salida.
     * @return fichaje abierto o null
     */
    Fichaje findUltimoSinCerrar(Long empleadoId);

    /**
     * Obtiene las horas trabajadas en una fecha.
     * @return horas totales del día
     */
    double obtenerHorasTotalesPorEmpleadoYFecha(Long empleadoId, LocalDate fecha);

    /**
     * Obtiene fichajes con olvido de salida.
     * @return lista de fichajes incompletos
     */
    List<Fichaje> findFichajesConOlvido(Long empleadoId);

    /**
     * Obtiene fichajes de un mes concreto.
     * @return lista de fichajes del mes
     */
    List<Fichaje> findByEmpleadoAndMonth(Long empleadoId, String anioMes);

    /**
     * Obtiene fichajes de un año concreto.
     * @return lista de fichajes del año
     */
    List<Fichaje> findByEmpleadoAndYear(Long empleadoId, int anio);

    /**
     * Obtiene los fichajes de la semana actual.
     * @return lista de fichajes de la semana
     */
    List<Fichaje> findByEmpleadoSemanaActual(Long empleadoId);
}
