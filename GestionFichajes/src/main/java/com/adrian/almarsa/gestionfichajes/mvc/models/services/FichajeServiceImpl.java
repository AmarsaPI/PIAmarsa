package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IFichajeDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Fichaje;

/**
 * Servicio para la gestión de fichajes.
 */
@Service
public class FichajeServiceImpl implements IFichajeService {

    @Autowired
    private IFichajeDAO fichajeDAO;

    /**
     * Obtiene todos los fichajes.
     * @return lista de fichajes
     */
    @Override
    @Transactional(readOnly = true)
    public List<Fichaje> findAll() {
        return (List<Fichaje>) fichajeDAO.findAll();
    }

    /**
     * Busca un fichaje por ID.
     * @return fichaje encontrado o null
     */
    @Override
    @Transactional(readOnly = true)
    public Fichaje findById(Long id) {
        return fichajeDAO.findById(id).orElse(null);
    }

    /**
     * Elimina un fichaje por ID.
     */
    @Override
    @Transactional
    public void delete(Long id) {
        fichajeDAO.deleteById(id);
    }

    /**
     * Registra la entrada de un empleado.
     * @return fichaje creado
     */
    @Override
    @Transactional
    public Fichaje registrarEntrada(Fichaje fichaje) {
        Long empleadoId = fichaje.getEmpleado().getId();
        Optional<Fichaje> fichajeAbierto =
                fichajeDAO.findFirstByEmpleadoIdAndFechaSalidaIsNullOrderByIdDesc(empleadoId);

        if (fichajeAbierto.isPresent()) {
            Fichaje activo = fichajeAbierto.get();
            LocalDateTime ahora = LocalDateTime.now();

            boolean esDeHoy = activo.getFechaEntrada().toLocalDate().equals(ahora.toLocalDate());
            if (esDeHoy) {
                throw new RuntimeException("Ya tienes una jornada iniciada hoy.");
            }
        }

        if (fichaje.getFechaEntrada() == null) {
            fichaje.setFechaEntrada(LocalDateTime.now());
        }
        return fichajeDAO.save(fichaje);
    }

    /**
     * Registra la salida de un fichaje.
     * @return fichaje actualizado
     */
    @Override
    @Transactional
    public Fichaje registrarSalida(Long fichajeId) {
        Fichaje fichaje = fichajeDAO.findById(fichajeId)
                .orElseThrow(() -> new RuntimeException("Fichaje no encontrado"));

        if (fichaje.getFechaSalida() != null) {
            throw new RuntimeException("El fichaje ya tiene salida registrada");
        }

        fichaje.setFechaSalida(LocalDateTime.now());
        return fichajeDAO.save(fichaje);
    }

    /**
     * Guarda o actualiza un fichaje.
     * @return fichaje guardado
     */
    @Override
    @Transactional
    public Fichaje save(Fichaje fichaje) {
        return fichajeDAO.save(fichaje);
    }

    /**
     * Obtiene los fichajes de un empleado.
     * @return lista de fichajes
     */
    @Override
    @Transactional(readOnly = true)
    public List<Fichaje> findByEmpleado(Long empleadoId) {
        return fichajeDAO.findByEmpleado_Id(empleadoId);
    }

    /**
     * Obtiene los fichajes de la semana actual.
     * @return lista de fichajes de la semana
     */
    @Transactional(readOnly = true)
    public List<Fichaje> findByEmpleadoSemanaActual(Long empleadoId) {
        LocalDateTime monday = LocalDateTime.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .minusDays(1);

        LocalDateTime sunday = LocalDateTime.now()
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                .plusDays(1);

        return fichajeDAO.findByEmpleado_Id(empleadoId).stream()
                .filter(f -> f.getFechaEntrada().isAfter(monday)
                          && f.getFechaEntrada().isBefore(sunday))
                .toList();
    }

    /**
     * Obtiene el último fichaje sin cerrar.
     * @return fichaje abierto o null
     */
    @Override
    @Transactional(readOnly = true)
    public Fichaje findUltimoSinCerrar(Long empleadoId) {
        return fichajeDAO.findFirstByEmpleadoIdAndFechaSalidaIsNullOrderByIdDesc(empleadoId)
                .orElse(null);
    }

    /**
     * Obtiene fichajes con olvido de salida.
     * @return lista de fichajes incompletos
     */
    @Override
    @Transactional(readOnly = true)
    public List<Fichaje> findFichajesConOlvido(Long empleadoId) {
        return fichajeDAO.findFichajesConOlvido(empleadoId);
    }

    /**
     * Obtiene las horas totales trabajadas en una fecha.
     * @return horas trabajadas
     */
    @Override
    @Transactional(readOnly = true)
    public double obtenerHorasTotalesPorEmpleadoYFecha(Long empleadoId, LocalDate fecha) {
        List<Fichaje> fichajesDelDia = fichajeDAO.findByEmpleadoIdAndFecha(empleadoId, fecha);

        if (fichajesDelDia == null || fichajesDelDia.isEmpty()) {
            return 0.0;
        }

        long minutosTotales = 0;

        for (Fichaje f : fichajesDelDia) {
            if (f.getFechaEntrada() != null && f.getFechaSalida() != null) {
                minutosTotales += ChronoUnit.MINUTES.between(
                        f.getFechaEntrada(), f.getFechaSalida()
                );
            }
        }

        return minutosTotales / 60.0;
    }

    /**
     * Obtiene fichajes de un mes concreto.
     * @return lista de fichajes del mes
     */
    public List<Fichaje> findByEmpleadoAndMonth(Long empleadoId, String anioMes) {
        return fichajeDAO.findByEmpleadoAndMonth(empleadoId, anioMes);
    }

    /**
     * Obtiene fichajes de un año concreto.
     * @return lista de fichajes del año
     */
    @Override
    @Transactional(readOnly = true)
    public List<Fichaje> findByEmpleadoAndYear(Long empleadoId, int anio) {
        return fichajeDAO.findByEmpleadoAndYear(empleadoId, anio);
    }
}
