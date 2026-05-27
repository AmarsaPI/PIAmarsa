package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.util.List;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;

/**
 * Servicio para la gestión de empleados.
 */
public interface IEmpleadoService {

    /**
     * Obtiene todos los empleados activos.
     * @return lista de empleados activos
     */
    List<Empleado> findAll();

    /**
     * Guarda o actualiza un empleado, cifrando la contraseña si corresponde.
     * @return empleado guardado
     */
    Empleado save(Empleado empleado);

    /**
     * Busca un empleado por ID.
     * @return empleado encontrado o null
     */
    Empleado findById(Long id);

    /**
     * Elimina un empleado por ID.
     */
    void delete(Long id);

    /**
     * Busca un empleado por email.
     * @return empleado encontrado o null
     */
    Empleado findByEmail(String email);

    /**
     * Marca un empleado como inactivo.
     */
    void darDeBaja(Long id);

    /**
     * Obtiene todos los empleados, incluidos los inactivos.
     * @return lista completa de empleados
     */
    List<Empleado> findAllIncluyendoInactivos();
}
