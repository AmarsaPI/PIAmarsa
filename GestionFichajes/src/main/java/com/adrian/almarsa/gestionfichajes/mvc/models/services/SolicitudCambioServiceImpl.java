package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IFichajeDAO; // Necesario para actualizar
import com.adrian.almarsa.gestionfichajes.mvc.models.dao.ISolicitudCambioDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class SolicitudCambioServiceImpl implements ISolicitudCambioService {

    @Autowired private ISolicitudCambioDAO solicitudDAO;
    @Autowired private IFichajeDAO fichajeDAO;
    @Autowired private IHorarioService horarioService;

    @Override
    @Transactional
    public void guardar(SolicitudCambio solicitud) { solicitudDAO.save(solicitud); }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudCambio> findPendientes() { 
        return solicitudDAO.findByEstado(EstadoSolicitud.PENDIENTE); 
    }

    @Override
    @Transactional
    public void aprobarSolicitud(Long solicitudId) {
        SolicitudCambio s = solicitudDAO.findById(solicitudId).orElse(null);
        if (s == null) return;

        if (s.getFichaje() != null) {
            // --- Lógica para fichajes pasados ---
            Fichaje f = s.getFichaje();
            // ... (tu lógica de actualización)
        } else {
            // --- Lógica para cambios de turno futuros (CASO 2) ---
            // Aquí NO usamos s.getFichaje(), usamos s.getEmpleado()
            horarioService.actualizarTurnoProgramado(
                s.getEmpleado().getId(), // <-- Ahora es seguro usar s.getEmpleado()
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