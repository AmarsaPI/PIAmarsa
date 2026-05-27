package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IAdminDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Admin;

/**
 * Servicio para la gestión de administradores.
 */
@Service
public class AdminServiceImpl implements IAdminService {

    @Autowired
    private IAdminDAO adminDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Obtiene todos los administradores.
     * @return lista de administradores
     */
    @Override
    @Transactional(readOnly = true)
    public List<Admin> findAll() {
        return (List<Admin>) adminDAO.findAll();
    }

    /**
     * Guarda un administrador cifrando su contraseña.
     * @return administrador guardado
     */
    @Override
    @Transactional
    public Admin save(Admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminDAO.save(admin);
    }

    /**
     * Busca un administrador por ID.
     * @return admin encontrado o null
     */
    @Override
    @Transactional(readOnly = true)
    public Admin findById(Long id) {
        return adminDAO.findById(id).orElse(null);
    }

    /**
     * Elimina un administrador por ID.
     */
    @Override
    @Transactional
    public void delete(Long id) {
        adminDAO.deleteById(id);
    }

    /**
     * Busca un administrador por email.
     * @return admin encontrado o null
     */
    @Override
    @Transactional(readOnly = true)
    public Admin findByEmail(String email) {
        return adminDAO.findByEmail(email).orElse(null);
    }
}

