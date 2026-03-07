package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.util.List;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;

// Interfaz que define las operaciones de negocio para la entidad Empleado
public interface IEmpleadoService {
	
    // Recupera la lista completa de empleados registrados
    public List<Empleado> findAll();
    
    // Guarda un nuevo empleado o actualiza uno existente (incluye el hash de contraseña)
    public Empleado save(Empleado empleado);

    // Busca un empleado específico por su clave primaria (ID)
    public Empleado findById(Long id);
    
    // Elimina un empleado del sistema permanentemente
    public void delete(Long id);
}