package com.adrian.almarsa.gestionfichajes.mvc.models.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Fichaje;

/**
 * Repositorio encargado de gestionar los registros de fichaje
 * (entradas y salidas) de los empleados.
 *
 * Incluye consultas específicas para detectar fichajes abiertos,
 * obtener historiales por fecha, localizar olvidos y generar
 * informes mensuales o anuales.
 */
public interface IFichajeDAO extends CrudRepository<Fichaje, Long> {

    /**
     * Busca el último fichaje abierto de un empleado, es decir,
     * aquel que todavía no tiene fecha de salida registrada.
     * Se usa para evitar que un empleado fiche entrada dos veces seguidas.
     *
     * @param empleadoId ID del empleado
     * @return Optional con el fichaje abierto o vacío si no existe
     */
    Optional<Fichaje> findFirstByEmpleadoIdAndFechaSalidaIsNullOrderByIdDesc(Long empleadoId);

    /**
     * Obtiene el historial completo de fichajes de un empleado.
     *
     * @param empleadoId ID del empleado
     * @return lista de fichajes ordenados según el criterio por defecto
     */
    List<Fichaje> findByEmpleado_Id(Long empleadoId);

    /**
     * Obtiene los fichajes de un empleado en una fecha concreta.
     * Se compara solo la parte de fecha (sin horas).
     *
     * @param empleadoId ID del empleado
     * @param fecha fecha a consultar
     * @return lista de fichajes realizados ese día
     */
    @Query("SELECT f FROM Fichaje f WHERE f.empleado.id = :empleadoId AND FUNCTION('DATE', f.fechaEntrada) = :fecha")
    List<Fichaje> findByEmpleadoIdAndFecha(@Param("empleadoId") Long empleadoId,
                                           @Param("fecha") LocalDate fecha);

    /**
     * Busca fichajes abiertos cuya fecha de entrada pertenece a días anteriores.
     * Se usa para detectar olvidos de fichar salida.
     *
     * @param empleadoId ID del empleado
     * @return lista de fichajes sin cerrar de días pasados
     */
    @Query("SELECT f FROM Fichaje f WHERE f.empleado.id = :empleadoId " +
           "AND f.fechaSalida IS NULL " +
           "AND FUNCTION('DATE', f.fechaEntrada) < CURRENT_DATE")
    List<Fichaje> findFichajesConOlvido(@Param("empleadoId") Long empleadoId);

    /**
     * Obtiene todos los fichajes de un empleado en un mes concreto.
     * El parámetro debe tener formato 'YYYY-MM'.
     *
     * @param empleadoId ID del empleado
     * @param anioMes cadena con año y mes (ej: '2025-03')
     * @return lista de fichajes ordenados por fecha de entrada
     */
    @Query("SELECT f FROM Fichaje f WHERE f.empleado.id = :empleadoId " +
           "AND TO_CHAR(f.fechaEntrada, 'YYYY-MM') = :anioMes " +
           "ORDER BY f.fechaEntrada ASC")
    List<Fichaje> findByEmpleadoAndMonth(@Param("empleadoId") Long empleadoId,
                                         @Param("anioMes") String anioMes);

    /**
     * Obtiene todos los fichajes de un empleado en un año concreto.
     *
     * @param empleadoId ID del empleado
     * @param anio año a consultar
     * @return lista de fichajes del año indicado
     */
    @Query("SELECT f FROM Fichaje f WHERE f.empleado.id = :empleadoId AND YEAR(f.fechaEntrada) = :anio")
    List<Fichaje> findByEmpleadoAndYear(@Param("empleadoId") Long empleadoId,
                                        @Param("anio") int anio);
}
