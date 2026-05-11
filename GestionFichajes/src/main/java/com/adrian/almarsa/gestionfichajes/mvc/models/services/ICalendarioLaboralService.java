package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.util.List;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.CalendarioLaboral;

public interface ICalendarioLaboralService {

	// Recupera todos los calendarios laborales configurados en el sistema
    public List<CalendarioLaboral> findAll();
	
    // Crea un nuevo calendario laboral o actualiza uno existente para un empleado
    public CalendarioLaboral save(CalendarioLaboral horario);

    // Obtiene un registro de calendario laboral mediante su ID
    public CalendarioLaboral findById(Long id);
	
    // Elimina un calendario laboral
    public void delete(Long id);
	
    // Recupera el calendario laboral de un empleado concreto
    public CalendarioLaboral findByEmpleado(Long empleadoId);
}
