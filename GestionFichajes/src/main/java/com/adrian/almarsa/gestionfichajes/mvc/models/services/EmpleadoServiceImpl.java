package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IEmpleadoDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;

/**
 * Servicio para la gestión de empleados.
 */
@Service
public class EmpleadoServiceImpl implements IEmpleadoService {

    @Autowired
    private IEmpleadoDAO empleadoDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Obtiene todos los empleados activos.
     * @return lista de empleados activos
     */
    @Override
    @Transactional(readOnly = true)
    public List<Empleado> findAll() {
        return empleadoDAO.findByActivoTrue();
    }

    /**
     * Obtiene todos los empleados, incluidos los inactivos.
     * @return lista completa de empleados
     */
    @Override
    @Transactional(readOnly = true)
    public List<Empleado> findAllIncluyendoInactivos() {
        return (List<Empleado>) empleadoDAO.findAll();
    }

    /**
     * Guarda un empleado, cifrando la contraseña si corresponde.
     * @return empleado guardado
     */
    @Override
    @Transactional
    public Empleado save(Empleado empleado) {

        if (empleado.getId() != null) {
            Empleado empleadoExistente = empleadoDAO.findById(empleado.getId()).orElse(null);

            if (empleadoExistente != null) {
                if (empleado.getPassword() == null || empleado.getPassword().isEmpty()) {
                    empleado.setPassword(empleadoExistente.getPassword());
                } else {
                    empleado.setPassword(passwordEncoder.encode(empleado.getPassword()));
                }
            }
        } else {
            empleado.setPassword(passwordEncoder.encode(empleado.getPassword()));
        }

        return empleadoDAO.save(empleado);
    }

    /**
     * Busca un empleado por ID.
     * @return empleado encontrado o null
     */
    @Override
    @Transactional(readOnly = true)
    public Empleado findById(Long id) {
        return empleadoDAO.findById(id).orElse(null);
    }

    /**
     * Elimina un empleado por ID.
     */
    @Override
    @Transactional
    public void delete(Long id) {
        empleadoDAO.deleteById(id);
    }

    /**
     * Busca un empleado por email.
     * @return empleado encontrado o null
     */
    @Override
    @Transactional(readOnly = true)
    public Empleado findByEmail(String email) {
        return empleadoDAO.findByEmail(email).orElse(null);
    }

    /**
     * Marca un empleado como inactivo.
     */
    @Override
    @Transactional
    public void darDeBaja(Long id) {
        Empleado emp = empleadoDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        emp.setActivo(false);
        empleadoDAO.save(emp);
    }
}
