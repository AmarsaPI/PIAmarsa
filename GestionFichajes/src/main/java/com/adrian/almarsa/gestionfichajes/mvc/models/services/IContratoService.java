package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.time.LocalDate;
import java.util.List;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Contrato;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;

public interface IContratoService {
    Contrato guardarContrato(Contrato contrato);
    List<Contrato> obtenerContratosPorEmpleado(Empleado empleado);
    Contrato obtenerContratoActivo(Empleado empleado, LocalDate fecha);
    void eliminarContrato(Long id);
}