package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.util.List;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Admin;

public interface IAdminService {

	// Recupera la lista completa de admins registrados
    public List<Admin> findAll();
    
    // Guarda un nuevo admin o actualiza uno existente (incluye el hash de contraseña)
    public Admin save(Admin admin);

    // Busca un admin específico por su clave primaria (ID)
    public Admin findById(Long id);
    
    // Elimina un admin del sistema permanentemente
    public void delete(Long id);
    
    public Admin findByEmail(String email);
}
