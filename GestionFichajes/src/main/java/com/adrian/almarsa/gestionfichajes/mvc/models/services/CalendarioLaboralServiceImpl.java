package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.ICalendarioLaboralDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.CalendarioLaboral;

/**
 * Servicio para la gestión de calendarios laborales.
 */
@Service
public class CalendarioLaboralServiceImpl implements ICalendarioLaboralService {

    @Autowired
    private ICalendarioLaboralDAO calendarioDAO;

    /**
     * Obtiene todos los calendarios laborales.
     * @return lista de calendarios
     */
    @Override
    @Transactional(readOnly = true)
    public List<CalendarioLaboral> findAll() {
        return (List<CalendarioLaboral>) calendarioDAO.findAll();
    }

    /**
     * Guarda un calendario laboral.
     * @return calendario guardado
     */
    @Override
    @Transactional
    public CalendarioLaboral save(CalendarioLaboral calendarioLaboral) {
        return calendarioDAO.save(calendarioLaboral);
    }

    /**
     * Busca un calendario laboral por ID.
     * @return calendario encontrado o null
     */
    @Override
    @Transactional(readOnly = true)
    public CalendarioLaboral findById(Long id) {
        return calendarioDAO.findById(id).orElse(null);
    }

    /**
     * Elimina un calendario laboral por ID.
     */
    @Override
    @Transactional
    public void delete(Long id) {
        calendarioDAO.deleteById(id);
    }

    /**
     * Obtiene el calendario laboral asignado a un empleado.
     * @return calendario del empleado o null
     */
    @Override
    @Transactional(readOnly = true)
    public CalendarioLaboral findByEmpleado(Long empleadoId) {
        return calendarioDAO.findByEmpleados_Id(empleadoId);
    }
}
