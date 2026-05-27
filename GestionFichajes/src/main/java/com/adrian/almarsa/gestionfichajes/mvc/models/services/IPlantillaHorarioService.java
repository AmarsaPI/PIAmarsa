package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.util.List;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.PlantillaHorario;

/**
 * Servicio para la gestión de plantillas de horarios.
 */
public interface IPlantillaHorarioService {

    /**
     * Obtiene todas las plantillas de horario.
     * @return lista de plantillas
     */
    List<PlantillaHorario> findAll();

    /**
     * Guarda o actualiza una plantilla de horario.
     * @return plantilla guardada
     */
    PlantillaHorario save(PlantillaHorario horario);

    /**
     * Busca una plantilla por ID.
     * @return plantilla encontrada o null
     */
    PlantillaHorario findById(Long id);

    /**
     * Elimina una plantilla por ID.
     */
    void delete(Long id);

    /**
     * Obtiene plantillas por nombre.
     * @return lista de plantillas coincidentes
     */
    List<PlantillaHorario> findByNombrePlantilla(String nombrePlantilla);
}
