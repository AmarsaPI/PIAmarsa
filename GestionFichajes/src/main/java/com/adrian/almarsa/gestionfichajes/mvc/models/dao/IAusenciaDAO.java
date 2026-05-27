package com.adrian.almarsa.gestionfichajes.mvc.models.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Ausencia;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.EstadoAusencia;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.TipoAusencia;

/**
 * Repositorio encargado de gestionar las ausencias de los empleados.
 * 
 * Proporciona métodos de consulta específicos para filtrar por empleado,
 * tipo de ausencia, estado o rangos de fechas. También incluye operaciones
 * personalizadas para detectar solapamientos y limpiar solicitudes.
 */
public interface IAusenciaDAO extends CrudRepository<Ausencia, Long> {
	
	/**
     * Obtiene todas las ausencias asociadas a un empleado concreto.
     *
     * @param empleado empleado del que se quieren obtener las ausencias
     * @return lista de ausencias del empleado
     */
	List<Ausencia> findByEmpleado(Empleado empleado);
	
	/**
     * Obtiene las ausencias de un empleado filtradas por tipo.
     *
     * @param empleadoId ID del empleado
     * @param tipo tipo de ausencia (vacaciones, baja, permiso…)
     * @return lista de ausencias que coinciden con el filtro
     */
    List<Ausencia> findByEmpleadoIdAndTipo(Long empleadoId, TipoAusencia tipo);
    
    /**
     * Obtiene todas las ausencias que se encuentran en un estado concreto.
     *
     * @param estado estado de la ausencia (pendiente, aprobada, rechazada)
     * @return lista de ausencias con ese estado
     */
    List<Ausencia> findByEstado(EstadoAusencia estado);

    /**
     * Busca ausencias activas en una fecha concreta.
     * Útil para comprobar si un empleado ya tiene un día reservado.
     *
     * @param empleado empleado a consultar
     * @param fecha fecha a verificar
     * @return lista de ausencias que cubren esa fecha
     */
    @Query("SELECT a FROM Ausencia a WHERE a.empleado = :empleado " +
           "AND :fecha BETWEEN a.fechaInicio AND a.fechaFin")
    List<Ausencia> findAusenciasEnFecha(@Param("empleado") Empleado empleado, @Param("fecha") LocalDate fecha);
    
    /**
     * Obtiene ausencias de un empleado dentro de un rango de fechas,
     * filtrando además por tipo.
     *
     * @param empleadoId ID del empleado
     * @param tipo tipo de ausencia
     * @param desde fecha de inicio del rango
     * @param hasta fecha de fin del rango
     * @return lista de ausencias dentro del rango indicado
     */
    List<Ausencia> findByEmpleadoIdAndTipoAndFechaInicioBetween(
            Long empleadoId, 
            TipoAusencia tipo, 
            LocalDate desde, 
            LocalDate hasta
        );
    
    /**
     * Elimina todas las ausencias de un empleado que coincidan con un tipo
     * y un estado concreto.  
     * Se usa, por ejemplo, para limpiar solicitudes rechazadas.
     *
     * @param empleadoId ID del empleado
     * @param tipo tipo de ausencia
     * @param estado estado de las ausencias a eliminar
     */    
    @Modifying
    void deleteByEmpleadoIdAndTipoAndEstado(Long empleadoId, TipoAusencia tipo, EstadoAusencia estado);
}
