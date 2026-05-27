package com.adrian.almarsa.gestionfichajes.mvc.models.dao;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.EstadoSolicitud;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.SolicitudCambio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * Repositorio encargado de gestionar las solicitudes de cambio de turno.
 *
 * Extiende de {@link CrudRepository} para ofrecer las operaciones básicas
 * de persistencia y añade un método específico para filtrar solicitudes
 * según su estado (pendiente, aprobada, rechazada).
 */
public interface ISolicitudCambioDAO extends CrudRepository<SolicitudCambio, Long> {

    /**
     * Obtiene todas las solicitudes de cambio que se encuentran
     * en un estado concreto.
     *
     * @param estado estado de la solicitud (pendiente, aprobada, rechazada)
     * @return lista de solicitudes que coinciden con el estado indicado
     */
    List<SolicitudCambio> findByEstado(EstadoSolicitud estado);
}
