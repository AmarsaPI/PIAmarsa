package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IFichajeDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.dto.EmpleadoBalanceDTO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para el cálculo de la bolsa de horas anual.
 */
@Service
public class BolsaHorasServiceImpl implements IBolsaHorasService {

    @Autowired private IHorarioService horarioService;
    @Autowired private IAusenciaService ausenciaService;
    @Autowired private IContratoService contratoService;
    @Autowired private IFichajeDAO fichajeDAO;

    private static final ZoneId ZONA_ESP = ZoneId.of("Europe/Madrid");

    /**
     * Calcula la bolsa anual acumulada de un empleado.
     * @return saldo de horas acumulado
     */
    @Override
    @Transactional(readOnly = true)
    public double calcularBolsaAnualAcumulada(Empleado empleado) {
        int anio = LocalDate.now(ZONA_ESP).getYear();

        List<Fichaje> fichajesAnio = fichajeDAO.findByEmpleadoAndYear(empleado.getId(), anio);
        List<Horario> turnosAnio = horarioService.findByEmpleado(empleado.getId());
        List<Ausencia> ausencias = ausenciaService.obtenerAusenciasPorEmpleado(empleado);

        double saldo = 0.0;
        LocalDate hoy = LocalDate.now(ZONA_ESP);

        for (LocalDate curr = LocalDate.of(anio, 1, 1); !curr.isAfter(hoy); curr = curr.plusDays(1)) {

            final LocalDate fechaActual = curr;

            double horasTeoricas = obtenerHorasComprometidas(empleado, fechaActual, turnosAnio);

            double horasFichadas = fichajesAnio.stream()
                    .filter(f -> f.getFechaEntrada() != null &&
                                 f.getFechaEntrada().toLocalDate().equals(fechaActual))
                    .mapToDouble(this::calcularDuracionFichaje)
                    .sum();

            saldo += (horasFichadas - horasTeoricas);
        }
        return saldo;
    }

    /**
     * Obtiene las horas previstas para un día concreto.
     * @return horas teóricas del día
     */
    private double obtenerHorasComprometidas(Empleado emp, LocalDate fecha, List<Horario> turnos) {

        if (ausenciaService.esEmpleadoAusente(emp, fecha)) {
            return 0.0;
        }

        LocalDate hoy = LocalDate.now();

        if (!fecha.isAfter(hoy)) {
            return turnos.stream()
                    .filter(t -> t.getFecha().equals(fecha))
                    .mapToDouble(this::calcularDuracion)
                    .findFirst().orElse(0.0);
        } else {
            Contrato contrato = contratoService.obtenerContratoActivo(emp, fecha);
            return (contrato != null) ? contrato.getMinutosTeoricosDiarios() / 60.0 : 0.0;
        }
    }

    /**
     * Calcula la duración total de un horario.
     * @return horas totales del turno
     */
    private double calcularDuracion(Horario h) {
        long minutos1 = java.time.Duration.between(h.getHoraInicio(), h.getHoraFin()).toMinutes();

        long minutos2 = 0;
        if (h.getHoraInicio2() != null && h.getHoraFin2() != null) {
            minutos2 = java.time.Duration.between(h.getHoraInicio2(), h.getHoraFin2()).toMinutes();
        }

        return (minutos1 + minutos2) / 60.0;
    }

    /**
     * Calcula la duración de un fichaje.
     * @return horas fichadas
     */
    private double calcularDuracionFichaje(Fichaje f) {
        if (f.getFechaEntrada() == null || f.getFechaSalida() == null) {
            return 0.0;
        }
        long minutos = java.time.Duration.between(f.getFechaEntrada(), f.getFechaSalida()).toMinutes();
        return minutos / 60.0;
    }

    /**
     * Obtiene las horas previstas acumuladas del año.
     * @return total de horas previstas
     */
    @Override
    public double obtenerHorasPrevistasTotales(Empleado emp) {
        LocalDate hoy = LocalDate.now(ZONA_ESP);
        int anio = hoy.getYear();
        LocalDate inicioAnio = LocalDate.of(anio, 1, 1);

        List<Horario> turnosAnio = horarioService.findByEmpleado(emp.getId());
        double totalPrevisto = 0.0;

        for (LocalDate curr = inicioAnio; !curr.isAfter(hoy); curr = curr.plusDays(1)) {
            totalPrevisto += obtenerHorasComprometidas(emp, curr, turnosAnio);
        }
        return totalPrevisto;
    }
}
