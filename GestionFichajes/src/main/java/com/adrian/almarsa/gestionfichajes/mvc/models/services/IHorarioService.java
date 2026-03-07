package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.util.List;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Horario;

// Interfaz que define las operaciones para gestionar los turnos laborales
public interface IHorarioService {

    // Recupera todos los horarios configurados en el sistema
    public List<Horario> findAll();
	
    // Crea un nuevo turno o actualiza uno existente para un empleado
    public Horario save(Horario horario);

    // Obtiene un registro de horario específico mediante su ID
    public Horario findById(Long id);
	
    // Elimina la asignación de un horario
    public void delete(Long id);
	
    // Recupera la planificación semanal (lista de horarios) de un empleado concreto
    List<Horario> findByEmpleado(Long empleadoId);
}