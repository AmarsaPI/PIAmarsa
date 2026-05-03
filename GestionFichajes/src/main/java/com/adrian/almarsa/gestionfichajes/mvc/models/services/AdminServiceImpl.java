package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IAdminDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Admin;

@Service
public class AdminServiceImpl implements IAdminService {

	@Autowired
	private IAdminDAO adminDAO;
	
	@Autowired
	private PasswordEncoder passwordEncoder; // Inyectado desde SecurityConfig
	
	@Override
	@Transactional(readOnly = true) // Optimiza la consulta al ser solo de lectura
	public List<Admin> findAll() {
		return (List<Admin>) adminDAO.findAll();
	}
	
	@Override
	@Transactional
	public Admin save(Admin admin) {
		// Cifra la contraseña en texto plano a BCrypt antes de persistir en la DB
		// Esto es obligatorio para que coincida con lo que espera Spring Security
		admin.setPassword(passwordEncoder.encode(admin.getPassword()));
		return adminDAO.save(admin);
	}
	
	@Override
	@Transactional(readOnly = true) 
	public Admin findById(Long id) {
		// Retorna el admin o null si no existe (manejado por el Controller)
		return adminDAO.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		adminDAO.deleteById(id);
	}
	@Override
	@Transactional(readOnly = true)
	public Admin findByEmail(String email) {
	    return adminDAO.findByEmail(email).orElse(null);
	}
}
