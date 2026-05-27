package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IAusenciaDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IHorarioDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Ausencia;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.TipoAusencia;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.EstadoAusencia;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio para la gestión de ausencias.
 */
@Service
public class AusenciaServiceImpl implements IAusenciaService {

    @Autowired
    private IAusenciaDAO ausenciaDAO;

    @Autowired
    private IHorarioDAO horarioRealDAO;

    /**
     * Registra una ausencia.
     * @return ausencia guardada
     */
    @Override
    @Transactional
    public Ausencia registrarAusencia(Ausencia ausencia) {
        return ausenciaDAO.save(ausencia);
    }

    /**
     * Obtiene las ausencias de un empleado.
     * @return lista de ausencias
     */
    @Override
    @Transactional(readOnly = true)
    public List<Ausencia> obtenerAusenciasPorEmpleado(Empleado empleado) {
        return ausenciaDAO.findByEmpleado(empleado);
    }

    /**
     * Indica si un empleado está ausente en una fecha.
     * @return true si está ausente
     */
    @Override
    @Transactional(readOnly = true)
    public boolean esEmpleadoAusente(Empleado empleado, LocalDate fecha) {
        List<Ausencia> ausencias = ausenciaDAO.findAusenciasEnFecha(empleado, fecha);
        return !ausencias.isEmpty();
    }

    /**
     * Elimina una ausencia por ID.
     */
    @Override
    @Transactional
    public void eliminarAusencia(Long id) {
        ausenciaDAO.deleteById(id);
    }

    /**
     * Obtiene ausencias por empleado y tipo dentro del rango dinámico.
     * @return lista filtrada de ausencias
     */
    @Override
    @Transactional(readOnly = true)
    public List<Ausencia> obtenerPorEmpleadoYTipo(Long empleadoId, TipoAusencia tipo) {
        int anioActual = LocalDate.now().getYear();
        LocalDate fechaDesde = LocalDate.of(anioActual - 1, 12, 1);
        LocalDate fechaHasta = LocalDate.of(anioActual + 1, 1, 31);

        return ausenciaDAO.findByEmpleadoIdAndTipoAndFechaInicioBetween(
                empleadoId, tipo, fechaDesde, fechaHasta
        );
    }

    /**
     * Obtiene ausencias por estado.
     * @return lista de ausencias
     */
    @Override
    @Transactional(readOnly = true)
    public List<Ausencia> obtenerPorEstado(EstadoAusencia estado) {
        return ausenciaDAO.findByEstado(estado);
    }

    /**
     * Busca una ausencia por ID.
     * @return ausencia encontrada o null
     */
    @Override
    @Transactional(readOnly = true)
    public Ausencia buscarPorId(Long id) {
        return ausenciaDAO.findById(id).orElse(null);
    }

    /**
     * Guarda una ausencia.
     */
    @Override
    @Transactional
    public void guardar(Ausencia ausencia) {
        ausenciaDAO.save(ausencia);
    }

    /**
     * Elimina horarios reales dentro del rango de la ausencia.
     */
    @Override
    @Transactional
    public void borrarHorariosEnRangoAusencia(Ausencia ausencia) {
        Empleado emp = ausencia.getEmpleado();
        LocalDate inicio = ausencia.getFechaInicio();
        LocalDate fin = ausencia.getFechaFin();

        horarioRealDAO.deleteByEmpleadoAndFechaBetween(emp, inicio, fin);
    }

    /**
     * Elimina ausencias rechazadas por empleado y tipo.
     */
    @Override
    @Transactional
    public void borrarRechazadasPorEmpleadoYTipo(Long empleadoId, TipoAusencia tipo) {
        ausenciaDAO.deleteByEmpleadoIdAndTipoAndEstado(
                empleadoId, tipo, EstadoAusencia.RECHAZADA
        );
    }

    /**
     * Indica si un empleado está ausente con ausencia aprobada o baja.
     * @return true si debe bloquearse el fichaje
     */
    @Override
    @Transactional(readOnly = true)
    public boolean esEmpleadoAusenteAprobado(Empleado empleado, LocalDate fecha) {
        List<Ausencia> ausencias = ausenciaDAO.findAusenciasEnFecha(empleado, fecha);

        return ausencias.stream().anyMatch(a -> {
            boolean esAprobada = a.getEstado() == EstadoAusencia.APROBADA;
            boolean esBaja = a.getTipo() == TipoAusencia.BAJA_MEDICA;
            boolean esPermiso = a.getTipo() == TipoAusencia.PERMISO_RETRIBUIDO;
            return esBaja || esAprobada || esPermiso;
        });
    }
}
