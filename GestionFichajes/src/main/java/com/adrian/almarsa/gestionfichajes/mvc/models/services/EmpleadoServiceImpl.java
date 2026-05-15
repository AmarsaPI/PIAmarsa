package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IEmpleadoDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;

// Implementación de la lógica de negocio para empleados
@Service
public class EmpleadoServiceImpl implements IEmpleadoService {

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
		if (empleado.getId() != null) {
	        // Buscamos el empleado que ya existe en la base de datos
	        Empleado empleadoExistente = empleadoDAO.findById(empleado.getId()).orElse(null);

	        if (empleadoExistente != null) {
	            // Si la contraseña que viene del formulario está vacía o es nula significa que no la queremos cambiar
	            if (empleado.getPassword() == null || empleado.getPassword().isEmpty()) {
	                // ...mantenemos la contraseña cifrada que ya teníamos
	                empleado.setPassword(empleadoExistente.getPassword());
	            } else {
	                // si se ha escrito una nueva, ciframos la nueva contraseña
	                empleado.setPassword(passwordEncoder.encode(empleado.getPassword()));
	            }
	        }
	    } else {
	        // alta nueva, ciframos la contraseña obligatoriamente
	        empleado.setPassword(passwordEncoder.encode(empleado.getPassword()));
	    }

	    return empleadoDAO.save(empleado);
	}
	
	@Override
	@Transactional(readOnly = true) 
	public Empleado findById(Long id) {
		// Retorna el empleado o null si no existe (manejado por el Controller)
		return empleadoDAO.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		empleadoDAO.deleteById(id);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Empleado findByEmail(String email) {
	    return empleadoDAO.findByEmail(email).orElse(null);
	}
}