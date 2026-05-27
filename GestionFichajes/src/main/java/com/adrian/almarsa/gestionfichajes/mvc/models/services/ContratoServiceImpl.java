package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IContratoDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Contrato;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;

import jakarta.transaction.Transactional;

/**
 * Servicio para la gestión de contratos.
 */
@Service
public class ContratoServiceImpl implements IContratoService {

    @Autowired
    private IContratoDAO contratoDAO;

    /**
     * Guarda un contrato.
     * @return contrato guardado
     */
    @Override
    @Transactional
    public Contrato guardarContrato(Contrato contrato) {
        return contratoDAO.save(contrato);
    }

    /**
     * Obtiene los contratos de un empleado.
     * @return lista de contratos
     */
    @Override
    public List<Contrato> obtenerContratosPorEmpleado(Empleado empleado) {
        return contratoDAO.findByEmpleado(empleado);
    }

    /**
     * Obtiene el contrato activo en una fecha.
     * @return contrato activo o null
     */
    @Override
    public Contrato obtenerContratoActivo(Empleado empleado, LocalDate fecha) {
        return contratoDAO.findContratoActivoEnFecha(empleado, fecha)
                .orElse(null);
    }

    /**
     * Elimina un contrato por ID.
     */
    @Override
    @Transactional
    public void eliminarContrato(Long id) {
        contratoDAO.deleteById(id);
    }
}
