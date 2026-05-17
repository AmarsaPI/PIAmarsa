package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.util.List;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.PlantillaHorario;

// Interfaz que define las operaciones para gestionar los turnos laborales
public interface IPlantillaHorarioService {

    // Recupera todos los horarios configurados en el sistema
    public List<PlantillaHorario> findAll();
	
    // Crea un nuevo turno o actualiza uno existente para un empleado
    public PlantillaHorario save(PlantillaHorario horario);

    // Obtiene un registro de horario específico mediante su ID
    public PlantillaHorario findById(Long id);
	
    // Elimina la asignación de un horario
    public void delete(Long id);
    
    List<PlantillaHorario> findByNombrePlantilla(String nombrePlantilla);
}