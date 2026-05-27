package com.adrian.almarsa.gestionfichajes.mvc.models.dao;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.PlantillaHorario;

/**
 * Repositorio encargado de gestionar las plantillas de horarios semanales.
 *
 * Permite realizar las operaciones básicas de persistencia y añade un método
 * específico para obtener todos los turnos pertenecientes a una plantilla
 * concreta según su nombre.
 */
public interface IPlantillaHorarioDAO extends CrudRepository<PlantillaHorario, Long> {

    /**
     * Obtiene todos los registros de una plantilla de horarios identificada
     * por su nombre.  
     * Cada plantilla suele contener varios días (ej: "Mañana", "Tarde", "Noche").
     *
     * @param nombrePlantilla nombre de la plantilla
     * @return lista de turnos asociados a esa plantilla
     */
    List<PlantillaHorario> findByNombrePlantilla(String nombrePlantilla);
}
