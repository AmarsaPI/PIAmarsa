package com.adrian.almarsa.gestionfichajes.mvc.models.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Fichaje;

// Repositorio para gestionar la persistencia de los registros de entrada y salida
public interface IFichajeDAO extends CrudRepository<Fichaje, Long> {
	
    // Busca un fichaje abierto (sin fecha de salida) para un empleado específico
    // Se usa para validar que un empleado no fiche entrada dos veces seguidas
    Optional<Fichaje> findByEmpleadoIdAndFechaSalidaIsNull(Long empleadoId);

    // Obtiene el historial completo de fichajes de un empleado
    // El uso de "_" (Empleado_Id) garantiza que JPA busque por el ID del objeto Empleado
    List<Fichaje> findByEmpleado_Id(Long empleadoId);

}