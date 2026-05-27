package com.adrian.almarsa.gestionfichajes.mvc.models.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Admin;

/**
 * Repositorio para gestionar los datos de administradores.
 * 
 * Extiende de {@link CrudRepository} para ofrecer las operaciones básicas
 * de persistencia y añade un método específico para buscar administradores
 * por su email, útil en procesos de autenticación.
 */
public interface IAdminDAO extends CrudRepository<Admin, Long> {

	/**
     * Busca un administrador por su email.
     * Se devuelve un {@link Optional} para manejar de forma segura
     * el caso en el que no exista ningún registro con ese correo.
     *
     * @param email correo del administrador
     * @return Optional con el administrador encontrado o vacío si no existe
     */
    Optional<Admin> findByEmail(String email);
    
}
