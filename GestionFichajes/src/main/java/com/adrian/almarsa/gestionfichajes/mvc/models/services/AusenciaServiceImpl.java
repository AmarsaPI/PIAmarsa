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

@Service
public class AusenciaServiceImpl implements IAusenciaService {

    @Autowired
    private IAusenciaDAO ausenciaDAO;

    // 🎯 2. INYECTAMOS EL DAO DE HORARIOS
    @Autowired
    private IHorarioDAO horarioRealDAO; 

    @Override
    @Transactional
    public Ausencia registrarAusencia(Ausencia ausencia) {
        return ausenciaDAO.save(ausencia);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ausencia> obtenerAusenciasPorEmpleado(Empleado empleado) {
        return ausenciaDAO.findByEmpleado(empleado);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean esEmpleadoAusente(Empleado empleado, LocalDate fecha) {
        List<Ausencia> ausencias = ausenciaDAO.findAusenciasEnFecha(empleado, fecha);
        return !ausencias.isEmpty();
    }

    @Override
    @Transactional
    public void eliminarAusencia(Long id) {
        ausenciaDAO.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ausencia> obtenerPorEmpleadoYTipo(Long empleadoId, TipoAusencia tipo) {
        // 1. Obtenemos el año actual en el servidor (ej: 2026)
        int anioActual = LocalDate.now().getYear();
        
        // 2. Calculamos los límites dinámicos:
        LocalDate fechaDesde = LocalDate.of(anioActual - 1, 12, 1);
        
        // Hasta el 31 de enero del año siguiente (ej: 31-01-2027)
        LocalDate fechaHasta = LocalDate.of(anioActual + 1, 1, 31);
        
        // 3. Ejecutamos la nueva consulta filtrada
        return ausenciaDAO.findByEmpleadoIdAndTipoAndFechaInicioBetween(empleadoId, tipo, fechaDesde, fechaHasta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ausencia> obtenerPorEstado(EstadoAusencia estado) {
        return ausenciaDAO.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public Ausencia buscarPorId(Long id) {
        return ausenciaDAO.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void guardar(Ausencia ausencia) {
        ausenciaDAO.save(ausencia);
    }

    @Override
    @Transactional
    public void borrarHorariosEnRangoAusencia(Ausencia ausencia) {
        Empleado emp = ausencia.getEmpleado();
        LocalDate inicio = ausencia.getFechaInicio();
        LocalDate fin = ausencia.getFechaFin();
        
        horarioRealDAO.deleteByEmpleadoAndFechaBetween(emp, inicio, fin);
    }
    
    @Override
    @Transactional
    public void borrarRechazadasPorEmpleadoYTipo(Long empleadoId, TipoAusencia tipo) {
        // Llamamos al método de borrado filtrando por Empleado, Tipo y Estado Fijo 'RECHAZADA'
        ausenciaDAO.deleteByEmpleadoIdAndTipoAndEstado(empleadoId, tipo, EstadoAusencia.RECHAZADA);
    }

	@Override
	public boolean esEmpleadoAusenteAprobado(Empleado empleado, LocalDate fecha) {
		List<Ausencia> ausencias = ausenciaDAO.findAusenciasEnFecha(empleado, fecha);
	    
	    return ausencias.stream().anyMatch(a -> {
	        boolean esAprobada = (a.getEstado() == EstadoAusencia.APROBADA);
	        boolean esBaja = (a.getTipo() == TipoAusencia.BAJA_MEDICA); 
	        boolean esPermiso = (a.getTipo() == TipoAusencia.PERMISO_RETRIBUIDO);
	        
	        // Bloqueamos si es Baja (siempre) o si es Aprobada (vacaciones/otros)
	        return esBaja || esAprobada || esPermiso;
	    });
	}
}