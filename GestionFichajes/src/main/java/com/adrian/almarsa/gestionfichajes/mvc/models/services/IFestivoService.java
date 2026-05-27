package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.time.LocalDate;
import java.util.List;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Festivo;

/**
 * Servicio para la gestión de festivos.
 */
public interface IFestivoService {

    /**
     * Obtiene todos los festivos.
     * @return lista de festivos
     */
    List<Festivo> findAll();

    /**
     * Obtiene los festivos de un calendario laboral.
     * @return lista de festivos del calendario
     */
    List<Festivo> findByCalendario(Long calendarioId);

    /**
     * Obtiene los festivos asociados a un empleado.
     * @return lista de festivos del empleado
     */
    List<Festivo> findByEmpleado(Long empleadoId);

    /**
     * Guarda o actualiza un festivo.
     * @return festivo guardado
     */
    Festivo save(Festivo festivo);

    /**
     * Busca un festivo por ID.
     * @return festivo encontrado o null
     */
    Festivo findById(Long id);

    /**
     * Elimina un festivo por ID.
     */
    void delete(Long id);

    /**
     * Indica si existe un festivo en una fecha para un empleado.
     * @return true si la fecha es festiva
     */
    boolean existeFestivoEnFecha(LocalDate fecha, Long empleadoId);
}
