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

@Service
public class BolsaHorasServiceImpl implements IBolsaHorasService {

    @Autowired private IHorarioService horarioService;
    @Autowired private IAusenciaService ausenciaService;
    @Autowired private IContratoService contratoService;
    @Autowired private IFichajeDAO fichajeDAO;
    
    private static final ZoneId ZONA_ESP = ZoneId.of("Europe/Madrid");

    @Override
    @Transactional(readOnly = true)
    public double calcularBolsaAnualAcumulada(Empleado empleado) {
        int anio = LocalDate.now(ZONA_ESP).getYear();
        
        // 1. Cargamos TODOS los fichajes del año de este empleado en 1 consulta
        List<Fichaje> fichajesAnio = fichajeDAO.findByEmpleadoAndYear(empleado.getId(), anio);
        
        // 2. Cargamos turnos y ausencias (si los servicios ya los tienen optimizados, genial)
        List<Horario> turnosAnio = horarioService.findByEmpleado(empleado.getId());
        List<Ausencia> ausencias = ausenciaService.obtenerAusenciasPorEmpleado(empleado);

        double saldo = 0.0;
        LocalDate hoy = LocalDate.now(ZONA_ESP);

        // 3. Iteramos las fechas
        for (LocalDate curr = LocalDate.of(anio, 1, 1); !curr.isAfter(hoy); curr = curr.plusDays(1)) {
            
            // Captura el valor actual para que sea visible dentro de la Lambda
            final LocalDate fechaActual = curr; 
            
            double horasTeoricas = obtenerHorasComprometidas(empleado, fechaActual, turnosAnio);
            
            // Usamos 'fechaActual' en el filtro
            double horasFichadas = fichajesAnio.stream()
                .filter(f -> f.getFechaEntrada() != null && 
                             f.getFechaEntrada().toLocalDate().equals(fechaActual))
                .mapToDouble(this::calcularDuracionFichaje) 
                .sum();

            saldo += (horasFichadas - horasTeoricas);
        }
        return saldo;
    }

    private double obtenerHorasComprometidas(Empleado emp, LocalDate fecha, List<Horario> turnos) {
    	
        if (ausenciaService.esEmpleadoAusente(emp, fecha)) {
            return 0.0; 
        }
        LocalDate hoy = LocalDate.now();

        if (!fecha.isAfter(hoy)) {
            // Buscamos el turno del día
            return turnos.stream()
                .filter(t -> t.getFecha().equals(fecha))
                .mapToDouble(this::calcularDuracion) // Llamamos a nuestro helper
                .findFirst().orElse(0.0);
        } else {
            Contrato contrato = contratoService.obtenerContratoActivo(emp, fecha);
            return (contrato != null) ? contrato.getMinutosTeoricosDiarios() / 60.0 : 0.0;
        }
    }

    // AÑADIMOS DE NUEVO EL HELPER PARA CALCULAR LA DURACIÓN TOTAL
    private double calcularDuracion(Horario h) {
        // Bloque 1
        long minutos1 = java.time.Duration.between(h.getHoraInicio(), h.getHoraFin()).toMinutes();
        
        // Bloque 2 (si existe jornada partida)
        long minutos2 = 0;
        if (h.getHoraInicio2() != null && h.getHoraFin2() != null) {
            minutos2 = java.time.Duration.between(h.getHoraInicio2(), h.getHoraFin2()).toMinutes();
        }
        
        return (minutos1 + minutos2) / 60.0;
    }
    
    private double calcularDuracionFichaje(Fichaje f) {
        if (f.getFechaEntrada() == null || f.getFechaSalida() == null) {
            return 0.0;
        }
        // Calculamos la duración en minutos
        long minutos = java.time.Duration.between(f.getFechaEntrada(), f.getFechaSalida()).toMinutes();
        return minutos / 60.0;
    }
    
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