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

@Service
public class FestivoServiceImpl implements IFestivoService {

    @Autowired
    private IFestivoDAO festivoDAO;
    
    @Autowired
    @Lazy
    private IHorarioService horarioService;

    @Override
    @Transactional(readOnly = true)
    public List<Festivo> findAll() {
        return (List<Festivo>) festivoDAO.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Festivo> findByCalendario(Long calendarioId) {
        // Busca festivos asociados a un ID de calendario específico
        return festivoDAO.findByCalendarioId(calendarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Festivo> findByEmpleado(Long empleadoId) {
        // Este es el "atajo" que definimos en el DAO:
        // Busca festivos navegando por la relación Calendario -> Empleado
        return festivoDAO.findByCalendario_Empleados_Id(empleadoId);
    }

    @Override
    @Transactional(readOnly = true)
    public Festivo findById(Long id) {
        return festivoDAO.findById(id).orElse(null);
    }

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

    @Override
    @Transactional
    public void delete(Long id) {
        festivoDAO.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existeFestivoEnFecha(LocalDate fecha, Long empleadoId) {
        // Obtenemos todos los festivos del empleado
        List<Festivo> festivos = findByEmpleado(empleadoId);
        
        // Verificamos si alguno coincide con la fecha proporcionada
        return festivos.stream()
                .anyMatch(f -> f.getFecha().equals(fecha));
    }
}