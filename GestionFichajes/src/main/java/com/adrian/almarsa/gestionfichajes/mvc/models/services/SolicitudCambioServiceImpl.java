package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IFichajeDAO; 
import com.adrian.almarsa.gestionfichajes.mvc.models.dao.ISolicitudCambioDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Servicio para la gestión de solicitudes de cambio de turno.
 */
@Service
public class SolicitudCambioServiceImpl implements ISolicitudCambioService {

    @Autowired 
    private ISolicitudCambioDAO solicitudDAO;

    @Autowired 
    private IFichajeDAO fichajeDAO;

    @Autowired 
    private IHorarioService horarioService;

    /**
     * Guarda una solicitud de cambio.
     */
    @Override
    @Transactional
    public void guardar(SolicitudCambio solicitud) {
        solicitudDAO.save(solicitud);
    }

    /**
     * Obtiene las solicitudes pendientes.
     * @return lista de solicitudes en estado pendiente
     */
    @Override
    @Transactional(readOnly = true)
    public List<SolicitudCambio> findPendientes() {
        return solicitudDAO.findByEstado(EstadoSolicitud.PENDIENTE);
    }

    /**
     * Aprueba una solicitud de cambio y aplica la modificación correspondiente.
     * Maneja tanto fichajes pasados como turnos futuros.
     */
    @Override
    @Transactional
    public void aprobarSolicitud(Long solicitudId) {
        SolicitudCambio s = solicitudDAO.findById(solicitudId).orElse(null);
        if (s == null) return;

        if (s.getFichaje() != null) {
            // Lógica para fichajes pasados (pendiente de implementación)
            Fichaje f = s.getFichaje();
            // ...
        } else {
            // Lógica para cambios de turno futuros
            horarioService.actualizarTurnoProgramado(
                s.getEmpleado().getId(),
                s.getFechaTurno(),
                s.getHoraEntradaPropuesta(),
                s.getHoraSalidaPropuesta(),
                s.getHoraEntradaPropuesta2(),
                s.getHoraSalidaPropuesta2()
            );
        }

        if (s.getFechaTurno() == null) {
            throw new IllegalArgumentException("La solicitud no tiene una fecha asignada");
        }

        s.setEstado(EstadoSolicitud.APROBADA);
        solicitudDAO.save(s);
    }

    /**
     * Rechaza una solicitud de cambio.
     */
    @Override
    @Transactional
    public void rechazarSolicitud(Long solicitudId) {
        SolicitudCambio s = solicitudDAO.findById(solicitudId).orElse(null);
        if (s != null) {
            s.setEstado(EstadoSolicitud.RECHAZADA);
            solicitudDAO.save(s);
        }
    }
}
