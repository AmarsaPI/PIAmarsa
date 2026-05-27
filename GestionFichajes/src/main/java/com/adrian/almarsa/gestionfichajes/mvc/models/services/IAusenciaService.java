package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Ausencia;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.TipoAusencia;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.EstadoAusencia;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio para la gestión de ausencias.
 */
public interface IAusenciaService {

    /**
     * Registra una ausencia.
     * @return ausencia guardada
     */
    Ausencia registrarAusencia(Ausencia ausencia);

    /**
     * Obtiene las ausencias de un empleado.
     * @return lista de ausencias
     */
    List<Ausencia> obtenerAusenciasPorEmpleado(Empleado empleado);

    /**
     * Indica si un empleado está ausente en una fecha.
     * @return true si está ausente
     */
    boolean esEmpleadoAusente(Empleado empleado, LocalDate fecha);

    /**
     * Elimina una ausencia por ID.
     */
    void eliminarAusencia(Long id);

    /**
     * Obtiene ausencias por empleado y tipo.
     * @return lista filtrada de ausencias
     */
    List<Ausencia> obtenerPorEmpleadoYTipo(Long empleadoId, TipoAusencia tipo);

    /**
     * Obtiene ausencias por estado.
     * @return lista de ausencias
     */
    List<Ausencia> obtenerPorEstado(EstadoAusencia estado);

    /**
     * Busca una ausencia por ID.
     * @return ausencia encontrada o null
     */
    Ausencia buscarPorId(Long id);

    /**
     * Guarda una ausencia.
     */
    void guardar(Ausencia ausencia);

    /**
     * Elimina horarios reales dentro del rango de la ausencia.
     */
    void borrarHorariosEnRangoAusencia(Ausencia ausencia);

    /**
     * Elimina ausencias rechazadas por empleado y tipo.
     */
    void borrarRechazadasPorEmpleadoYTipo(Long empleadoId, TipoAusencia tipo);

    /**
     * Indica si un empleado tiene una ausencia aprobada o baja en una fecha.
     * @return true si debe bloquearse el turno o fichaje
     */
    boolean esEmpleadoAusenteAprobado(Empleado empleado, LocalDate fecha);
}

