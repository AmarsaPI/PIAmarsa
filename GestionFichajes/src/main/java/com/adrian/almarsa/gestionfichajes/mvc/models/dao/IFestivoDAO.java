package com.adrian.almarsa.gestionfichajes.mvc.models.dao;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Festivo;

public interface IFestivoDAO extends CrudRepository<Festivo, Long> {
    
    // 1. Buscar festivos por el ID del calendario
    List<Festivo> findByCalendarioId(Long calendarioId);
    
    // 2. Buscar festivos de un empleado navegando por la relación
    List<Festivo> findByCalendario_Empleados_Id(Long empleadoId);
}