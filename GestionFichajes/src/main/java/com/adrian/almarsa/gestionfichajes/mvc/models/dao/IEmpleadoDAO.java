package com.adrian.almarsa.gestionfichajes.mvc.models.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;

/**
 * Repositorio encargado de gestionar la persistencia de los empleados.
 *
 * Extiende de {@link CrudRepository} para ofrecer las operaciones básicas
 * de CRUD y añade consultas específicas para autenticación y filtrado
 * de empleados activos.
 */
public interface IEmpleadoDAO extends CrudRepository<Empleado, Long> {

	/**
     * Busca un empleado por su email.
     * Se devuelve un {@link Optional} para manejar de forma segura
     * el caso en el que no exista ningún registro con ese correo.
     *
     * @param email correo del empleado
     * @return Optional con el empleado encontrado o vacío si no existe
     */
    Optional<Empleado> findByEmail(String email);
    
    /**
     * Obtiene todos los empleados que están marcados como activos.
     *
     * @return lista de empleados activos
     */
    List<Empleado> findByActivoTrue();
    
    /**
     * Devuelve todos los empleados almacenados en la base de datos.
     * Se redefine para obtener una lista en lugar de un Iterable.
     *
     * @return lista completa de empleados
     */
    List<Empleado> findAll();

}