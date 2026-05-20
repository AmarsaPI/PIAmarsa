package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;

public interface IBolsaHorasService {

    double calcularBolsaAnualAcumulada(Empleado empleado);
}