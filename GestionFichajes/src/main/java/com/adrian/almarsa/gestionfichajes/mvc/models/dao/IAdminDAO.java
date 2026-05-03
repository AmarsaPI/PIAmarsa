package com.adrian.almarsa.gestionfichajes.mvc.models.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Admin;

public interface IAdminDAO extends CrudRepository<Admin, Long> {

    // Busca un admin por su email (necesario para la autenticación de Spring Security)
    // Retorna un Optional para manejar de forma segura si el usuario no existe
    Optional<Admin> findByEmail(String email);

}
