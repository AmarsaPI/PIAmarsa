package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BolsaHorasServiceImpl implements IBolsaHorasService {

    @Autowired
    private IAusenciaService ausenciaService;

    @Autowired
    private IFichajeService fichajeService;

    @Override
    @Transactional(readOnly = true)
    public double calcularBolsaAnualAcumulada(Empleado empleado) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioAnio = LocalDate.of(hoy.getYear(), 1, 1);
        
        double saldoAcumulado = 0.0;
        
        // 1. Traemos las ausencias del empleado de golpe para optimizar rendimiento
        List<Ausencia> ausencias = ausenciaService.obtenerAusenciasPorEmpleado(empleado);

        // 2. Barremos día a día desde el 1 de enero hasta hoy
        LocalDate curr = inicioAnio;
        while (!curr.isAfter(hoy)) {
            
            // A. ¿Cuántas horas le exigía el sistema hoy?
            double horasTeoricas = obtenerTeoricasDelDia(empleado, curr, ausencias);
            
            // B. ¿Cuántas horas trabajó realmente?
            double horasFichadas = fichajeService.obtenerHorasTotalesPorEmpleadoYFecha(empleado.getId(), curr);

            // C. Sumamos o restamos la diferencia al saldo total
            saldoAcumulado += (horasFichadas - horasTeoricas);
            
            curr = curr.plusDays(1);
        }
        
        return saldoAcumulado;
    }

    /**
     * Evalúa el calendario y las ausencias para determinar las horas obligatorias de un día.
     */
    private double obtenerTeoricasDelDia(Empleado empleado, LocalDate fecha, List<Ausencia> ausencias) {
        // Fines de semana = no se trabaja (0.0 horas obligatorias)
        int diaSemana = fecha.getDayOfWeek().getValue();
        if (diaSemana == 6 || diaSemana == 7) return 0.0;

        // Festivos del calendario asignado = no se trabaja (0.0 horas obligatorias)
        if (empleado.getCalendario() != null && empleado.getCalendario().getFestivos() != null) {
            boolean esFestivo = empleado.getCalendario().getFestivos().stream()
                    .anyMatch(f -> f.getFecha().equals(fecha));
            if (esFestivo) return 0.0;
        }

        // Ausencias aprobadas (Vacaciones, Bajas, Permisos) = balance neutro (0.0 horas obligatorias)
        boolean tieneAusenciaValida = ausencias.stream()
                .filter(a -> a.getEstado() == EstadoAusencia.APROBADA)
                .anyMatch(a -> !fecha.isBefore(a.getFechaInicio()) && !fecha.isAfter(a.getFechaFin()));
        
        if (tieneAusenciaValida) return 0.0;

        // Jornada laboral estándar
        return 8.0;
    }
}