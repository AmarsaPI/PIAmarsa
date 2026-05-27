package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IFestivoDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Festivo;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Horario;

/**
 * Servicio para la gestión de festivos.
 */
@Service
public class FestivoServiceImpl implements IFestivoService {

    @Autowired
    private IFestivoDAO festivoDAO;

    @Autowired
    @Lazy
    private IHorarioService horarioService;

    /**
     * Obtiene todos los festivos.
     * @return lista de festivos
     */
    @Override
    @Transactional(readOnly = true)
    public List<Festivo> findAll() {
        return (List<Festivo>) festivoDAO.findAll();
    }

    /**
     * Obtiene festivos por calendario.
     * @return lista de festivos del calendario
     */
    @Override
    @Transactional(readOnly = true)
    public List<Festivo> findByCalendario(Long calendarioId) {
        return festivoDAO.findByCalendarioId(calendarioId);
    }

    /**
     * Obtiene festivos asociados a un empleado.
     * @return lista de festivos del empleado
     */
    @Override
    @Transactional(readOnly = true)
    public List<Festivo> findByEmpleado(Long empleadoId) {
        return festivoDAO.findByCalendario_Empleados_Id(empleadoId);
    }

    /**
     * Busca un festivo por ID.
     * @return festivo encontrado o null
     */
    @Override
    @Transactional(readOnly = true)
    public Festivo findById(Long id) {
        return festivoDAO.findById(id).orElse(null);
    }

    /**
     * Guarda un festivo y elimina turnos reales en esa fecha.
     * @return festivo guardado
     */
    @Override
    @Transactional
    public Festivo save(Festivo festivo) {
        Festivo festivoGuardado = festivoDAO.save(festivo);

        List<Empleado> empleadosDelCalendario = festivo.getCalendario().getEmpleados();

        if (empleadosDelCalendario != null) {
            for (Empleado emp : empleadosDelCalendario) {
                Horario turno = horarioService.findByEmpleadoIdAndFecha(emp.getId(), festivo.getFecha());
                if (turno != null) {
                    horarioService.delete(turno.getId());
                }
            }
        }

        return festivoGuardado;
    }

    /**
     * Elimina un festivo por ID.
     */
    @Override
    @Transactional
    public void delete(Long id) {
        festivoDAO.deleteById(id);
    }

    /**
     * Indica si existe un festivo en una fecha para un empleado.
     * @return true si hay festivo
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existeFestivoEnFecha(LocalDate fecha, Long empleadoId) {
        List<Festivo> festivos = findByEmpleado(empleadoId);
        return festivos.stream().anyMatch(f -> f.getFecha().equals(fecha));
    }
}
