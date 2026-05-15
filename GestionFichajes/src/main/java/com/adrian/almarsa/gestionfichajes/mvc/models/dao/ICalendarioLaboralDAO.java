package com.adrian.almarsa.gestionfichajes.mvc.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.CalendarioLaboral;

public interface ICalendarioLaboralDAO extends CrudRepository<CalendarioLaboral, Long>{
	
	CalendarioLaboral findByEmpleados_Id(Long empleadoId);
}
