package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.util.List;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Admin;

/**
 * Servicio para la gestión de administradores.
 */
public interface IAdminService {

    /**
     * Obtiene todos los administradores.
     * @return lista de administradores
     */
    List<Admin> findAll();

    /**
     * Guarda o actualiza un administrador.
     * @return admin guardado
     */
    Admin save(Admin admin);

    /**
     * Busca un administrador por ID.
     * @return admin encontrado o null
     */
    Admin findById(Long id);

    /**
     * Elimina un administrador por ID.
     */
    void delete(Long id);

    /**
     * Busca un administrador por email.
     * @return admin encontrado o null
     */
    Admin findByEmail(String email);
}
