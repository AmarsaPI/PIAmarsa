package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.SolicitudCambio;
import java.util.List;

public interface ISolicitudCambioService {
    void guardar(SolicitudCambio solicitud);
    List<SolicitudCambio> findPendientes();
    void aprobarSolicitud(Long solicitudId);
    void rechazarSolicitud(Long solicitudId);
}