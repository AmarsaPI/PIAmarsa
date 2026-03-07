package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IEmpleadoDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;

// Implementación de la lógica de negocio para empleados
@Service
public class EmpleadoServiceImpl implements IEmpleadoService, UserDetailsService {

	@Autowired
	private IEmpleadoDAO empleadoDAO;
	
	@Autowired
	private PasswordEncoder passwordEncoder; // Inyectado desde SecurityConfig
	
	@Override
	@Transactional(readOnly = true) // Optimiza la consulta al ser solo de lectura
	public List<Empleado> findAll() {
		return (List<Empleado>) empleadoDAO.findAll();
	}
	
	@Override
	@Transactional
	public Empleado save(Empleado empleado) {
		// Cifra la contraseña en texto plano a BCrypt antes de persistir en la DB
		// Esto es obligatorio para que coincida con lo que espera Spring Security
		empleado.setPassword(passwordEncoder.encode(empleado.getPassword()));
		return empleadoDAO.save(empleado);
	}
	
	@Override
	@Transactional(readOnly = true) 
	public Empleado findById(Long id) {
		// Retorna el empleado o null si no existe (manejado por el Controller)
		return empleadoDAO.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Empleado> findByEmail(String email) {
		return empleadoDAO.findByEmail(email);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		empleadoDAO.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return empleadoDAO.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Email no encontrado: " + username));
	}
}