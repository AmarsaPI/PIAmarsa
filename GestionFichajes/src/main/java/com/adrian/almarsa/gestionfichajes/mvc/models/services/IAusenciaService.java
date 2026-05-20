package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Ausencia;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.TipoAusencia;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.EstadoAusencia;

import java.time.LocalDate;
import java.util.List;

public interface IAusenciaService {

    Ausencia registrarAusencia(Ausencia ausencia);
    List<Ausencia> obtenerAusenciasPorEmpleado(Empleado empleado);
    boolean esEmpleadoAusente(Empleado empleado, LocalDate fecha);
    void eliminarAusencia(Long id);

    List<Ausencia> obtenerPorEmpleadoYTipo(Long empleadoId, TipoAusencia tipo);
    List<Ausencia> obtenerPorEstado(EstadoAusencia estado);
    Ausencia buscarPorId(Long id);
    void guardar(Ausencia ausencia); 
    void borrarHorariosEnRangoAusencia(Ausencia ausencia);
    void borrarRechazadasPorEmpleadoYTipo(Long empleadoId, TipoAusencia tipo);
}
