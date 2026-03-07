package com.adrian.almarsa.gestionfichajes.mvc.models.dao;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;

// Repositorio para la gestión de persistencia de la entidad Empleado
public interface IEmpleadoDAO extends CrudRepository<Empleado, Long> {

    // Busca un empleado por su email (necesario para la autenticación de Spring Security)
    // Retorna un Optional para manejar de forma segura si el usuario no existe
    Optional<Empleado> findByEmail(String email);

}