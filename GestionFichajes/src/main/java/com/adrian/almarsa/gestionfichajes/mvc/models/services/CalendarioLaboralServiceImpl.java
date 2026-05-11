package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.ICalendarioLaboralDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.CalendarioLaboral;

@Service
public class CalendarioLaboralServiceImpl implements ICalendarioLaboralService {

	@Autowired
	private ICalendarioLaboralDAO calendarioDAO;
	
	// Obtiene todos los horarios del sistema (útil para administración)
	@Override
	@Transactional(readOnly = true)
	public List<CalendarioLaboral> findAll() {
		return (List<CalendarioLaboral>) calendarioDAO.findAll();
	}
	
	@Override
	@Transactional
	public CalendarioLaboral save(CalendarioLaboral calendarioLaboral) {
	    return calendarioDAO.save(calendarioLaboral);
	}
	
	// Recupera un calendario laboral individual por su ID
	@Override
	@Transactional(readOnly = true) 
	public CalendarioLaboral findById(Long id) {
		return calendarioDAO.findById(id).orElse(null);
	}
	
	// Elimina un calendario laboral específico de la base de datos
	@Override
	@Transactional
	public void delete(Long id) {
		calendarioDAO.deleteById(id);
	}
	
	@Override
	@Transactional(readOnly = true)
	public CalendarioLaboral findByEmpleado(Long empleadoId) {
	    return calendarioDAO.findByEmpleados_Id(empleadoId);
	}
}
