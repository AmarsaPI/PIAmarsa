package com.adrian.almarsa.gestionfichajes.mvc.models.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Horario;

/**
 * Repositorio encargado de gestionar los horarios reales asignados a los empleados.
 *
 * Incluye consultas específicas para obtener horarios por empleado, por rango de fechas,
 * eliminar semanas completas y generar informes anuales.
 */
public interface IHorarioDAO extends CrudRepository<Horario, Long> {

    /**
     * Obtiene todos los horarios asociados a un empleado concreto.
     *
     * @param empleadoId ID del empleado
     * @return lista de horarios del empleado
     */
    List<Horario> findByEmpleado_Id(Long empleadoId);

    /**
     * Obtiene los horarios comprendidos entre dos fechas.
     *
     * @param inicio fecha inicial del rango
     * @param fin fecha final del rango
     * @return lista de horarios dentro del rango indicado
     */
    List<Horario> findByFechaBetween(LocalDate inicio, LocalDate fin);

    /**
     * Elimina todos los horarios de un empleado dentro de un rango de fechas.
     * Se usa, por ejemplo, para limpiar una semana antes de volver a planificarla.
     *
     * @param empleado empleado al que pertenecen los horarios
     * @param fechaInicio fecha inicial del rango
     * @param fechaFin fecha final del rango
     */
    @Modifying
    void deleteByEmpleadoAndFechaBetween(Empleado empleado,
                                         LocalDate fechaInicio,
                                         LocalDate fechaFin);

    /**
     * Obtiene los horarios de un empleado en un año concreto.
     *
     * @param empleadoId ID del empleado
     * @param anio año a consultar
     * @return lista de horarios del año indicado
     */
    @Query("SELECT h FROM Horario h WHERE h.empleado.id = :empleadoId AND FUNCTION('YEAR', h.fecha) = :anio")
    List<Horario> findByEmpleadoIdAndYear(@Param("empleadoId") Long empleadoId,
                                          @Param("anio") int anio);

    /**
     * Busca un horario concreto de un empleado en una fecha específica.
     * Útil para evitar duplicados o para actualizar un día concreto.
     *
     * @param empleadoId ID del empleado
     * @param fecha fecha del horario
     * @return Optional con el horario encontrado o vacío si no existe
     */
    Optional<Horario> findByEmpleadoIdAndFecha(Long empleadoId, LocalDate fecha);
}

