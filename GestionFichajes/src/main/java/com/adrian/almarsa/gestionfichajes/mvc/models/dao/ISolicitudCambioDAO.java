package com.adrian.almarsa.gestionfichajes.mvc.models.dao;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.EstadoSolicitud;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.SolicitudCambio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ISolicitudCambioDAO extends CrudRepository<SolicitudCambio, Long> {
	
	List<SolicitudCambio> findByEstado(EstadoSolicitud estado);
}