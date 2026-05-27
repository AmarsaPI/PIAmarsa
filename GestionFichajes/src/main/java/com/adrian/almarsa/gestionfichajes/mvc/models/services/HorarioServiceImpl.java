package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IEmpleadoDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IHorarioDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Horario;

/**
 * Servicio para la gestión de horarios asignados a empleados.
 */
@Service
public class HorarioServiceImpl implements IHorarioService {

    @Autowired
    private IHorarioDAO horarioDAO;

    @Autowired
    private IAusenciaService ausenciaService;

    @Autowired
    @Lazy
    private IFestivoService festivoService;

    @Autowired
    private IEmpleadoDAO empleadoDAO;

    /**
     * Obtiene todos los horarios del sistema.
     * @return lista de horarios
     */
    @Override
    @Transactional(readOnly = true)
    public List<Horario> findAll() {
        return (List<Horario>) horarioDAO.findAll();
    }

    /**
     * Guarda o actualiza un horario, validando ausencias y festivos.
     * @return horario guardado
     */
    @Override
    @Transactional
    public Horario save(Horario horario) {
        Empleado emp = horario.getEmpleado();
        LocalDate fecha = horario.getFecha();

        if (ausenciaService.esEmpleadoAusenteAprobado(emp, fecha)) {
            throw new RuntimeException("No se puede asignar turno: el empleado tiene una ausencia aprobada.");
        }

        if (isFestivo(fecha, emp.getId())) {
            throw new RuntimeException("No se puede asignar turno: la fecha es festiva.");
        }

        if (horario.getHoraInicio() == null || horario.getHoraFin() == null) {
            throw new RuntimeException("Las horas del horario no pueden ser nulas");
        }

        return horarioDAO.save(horario);
    }

    /**
     * Busca un horario por ID.
     * @return horario encontrado o null
     */
    @Override
    @Transactional(readOnly = true)
    public Horario findById(Long id) {
        return horarioDAO.findById(id).orElse(null);
    }

    /**
     * Elimina un horario por ID.
     */
    @Override
    @Transactional
    public void delete(Long id) {
        horarioDAO.deleteById(id);
    }

    /**
     * Obtiene los horarios válidos de un empleado (sin ausencias aprobadas ni festivos).
     * @return lista filtrada de horarios
     */
    @Override
    @Transactional(readOnly = true)
    public List<Horario> findByEmpleado(Long empleadoId) {
        List<Horario> todos = horarioDAO.findByEmpleado_Id(empleadoId);

        if (todos.isEmpty()) return todos;

        return todos.stream()
                .filter(h -> {
                    Empleado emp = h.getEmpleado();
                    LocalDate fecha = h.getFecha();
                    boolean bloqueoAusencia = ausenciaService.esEmpleadoAusenteAprobado(emp, fecha);
                    boolean esFestivo = isFestivo(fecha, empleadoId);
                    return !bloqueoAusencia && !esFestivo;
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene horarios dentro de un rango de fechas.
     * @return lista de horarios
     */
    @Override
    @Transactional(readOnly = true)
    public List<Horario> findByFechaBetween(LocalDate inicio, LocalDate fin) {
        return horarioDAO.findByFechaBetween(inicio, fin);
    }

    /**
     * Indica si una fecha es festiva para un empleado.
     * @return true si es festivo
     */
    private boolean isFestivo(LocalDate fecha, Long empleadoId) {
        return festivoService.existeFestivoEnFecha(fecha, empleadoId);
    }

    /**
     * Obtiene un horario por empleado y fecha.
     * @return horario encontrado o null
     */
    @Override
    public Horario findByEmpleadoIdAndFecha(Long empleadoId, LocalDate fecha) {
        return horarioDAO.findByEmpleadoIdAndFecha(empleadoId, fecha).orElse(null);
    }

    /**
     * Actualiza o crea un turno programado para un empleado.
     */
    @Override
    @Transactional
    public void actualizarTurnoProgramado(Long empleadoId, LocalDate fecha,
                                          LocalTime nuevaEntrada, LocalTime nuevaSalida,
                                          LocalTime nuevaEntrada2, LocalTime nuevaSalida2) {

        Horario horario = findByEmpleadoIdAndFecha(empleadoId, fecha);

        if (horario != null) {
            horario.setHoraInicio(nuevaEntrada);
            horario.setHoraFin(nuevaSalida);
            horario.setHoraInicio2(nuevaEntrada2);
            horario.setHoraFin2(nuevaSalida2);
            horario.setFecha(fecha);
            save(horario);
        } else {
            Empleado empleado = empleadoDAO.findById(empleadoId)
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

            if (ausenciaService.esEmpleadoAusenteAprobado(empleado, fecha)) {
                throw new IllegalStateException("No se puede modificar el turno por ausencia aprobada.");
            }

            Horario nuevoHorario = new Horario();
            nuevoHorario.setEmpleado(empleado);
            nuevoHorario.setFecha(fecha);
            nuevoHorario.setHoraInicio(nuevaEntrada);
            nuevoHorario.setHoraFin(nuevaSalida);
            nuevoHorario.setHoraInicio2(nuevaEntrada2);
            nuevoHorario.setHoraFin2(nuevaSalida2);
            nuevoHorario.setTipo("LABORABLE");

            save(nuevoHorario);
        }
    }
}
