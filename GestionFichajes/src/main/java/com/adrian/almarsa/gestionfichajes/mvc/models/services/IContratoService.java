package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.time.LocalDate;
import java.util.List;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Contrato;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;

/**
 * Servicio para la gestión de contratos.
 */
public interface IContratoService {

    /**
     * Guarda un contrato.
     * @return contrato guardado
     */
    Contrato guardarContrato(Contrato contrato);

    /**
     * Obtiene los contratos de un empleado.
     * @return lista de contratos
     */
    List<Contrato> obtenerContratosPorEmpleado(Empleado empleado);

    /**
     * Obtiene el contrato activo en una fecha.
     * @return contrato activo o null
     */
    Contrato obtenerContratoActivo(Empleado empleado, LocalDate fecha);

    /**
     * Elimina un contrato por ID.
     */
    void eliminarContrato(Long id);
}
