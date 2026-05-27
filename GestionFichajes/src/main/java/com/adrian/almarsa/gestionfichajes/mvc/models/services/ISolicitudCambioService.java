package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.SolicitudCambio;
import java.util.List;

/**
 * Servicio para la gestión de solicitudes de cambio de turno.
 */
public interface ISolicitudCambioService {

    /**
     * Guarda una solicitud de cambio.
     */
    void guardar(SolicitudCambio solicitud);

    /**
     * Obtiene las solicitudes pendientes.
     * @return lista de solicitudes pendientes
     */
    List<SolicitudCambio> findPendientes();

    /**
     * Aprueba una solicitud de cambio.
     */
    void aprobarSolicitud(Long solicitudId);

    /**
     * Rechaza una solicitud de cambio.
     */
    void rechazarSolicitud(Long solicitudId);
}
