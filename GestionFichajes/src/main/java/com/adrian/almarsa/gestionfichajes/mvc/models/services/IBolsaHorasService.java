package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;

/**
 * Servicio para el cálculo de la bolsa de horas.
 */
public interface IBolsaHorasService {

    /**
     * Calcula la bolsa anual acumulada de un empleado.
     * @return saldo de horas acumulado
     */
    double calcularBolsaAnualAcumulada(Empleado empleado);

    /**
     * Obtiene las horas previstas totales del año hasta la fecha.
     * @return horas previstas acumuladas
     */
    double obtenerHorasPrevistasTotales(Empleado emp);
}
