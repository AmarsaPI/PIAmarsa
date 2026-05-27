package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IPlantillaHorarioDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.PlantillaHorario;

/**
 * Servicio para la gestión de plantillas de horarios.
 */
@Service
public class PlantillaHorarioServiceImpl implements IPlantillaHorarioService {

    @Autowired
    private IPlantillaHorarioDAO horarioDAO;

    /**
     * Obtiene todas las plantillas de horario.
     * @return lista de plantillas
     */
    @Override
    @Transactional(readOnly = true)
    public List<PlantillaHorario> findAll() {
        return (List<PlantillaHorario>) horarioDAO.findAll();
    }

    /**
     * Guarda o actualiza una plantilla de horario.
     * @return plantilla guardada
     */
    @Override
    @Transactional
    public PlantillaHorario save(PlantillaHorario horario) {
        if (horario.getHoraInicio() == null || horario.getHoraFin() == null) {
            throw new RuntimeException("Las horas del horario no pueden ser nulas");
        }
        return horarioDAO.save(horario);
    }

    /**
     * Busca una plantilla por ID.
     * @return plantilla encontrada o null
     */
    @Override
    @Transactional(readOnly = true)
    public PlantillaHorario findById(Long id) {
        return horarioDAO.findById(id).orElse(null);
    }

    /**
     * Elimina una plantilla por ID.
     */
    @Override
    @Transactional
    public void delete(Long id) {
        horarioDAO.deleteById(id);
    }

    /**
     * Obtiene plantillas por nombre.
     * @return lista de plantillas coincidentes
     */
    @Override
    @Transactional(readOnly = true)
    public List<PlantillaHorario> findByNombrePlantilla(String nombrePlantilla) {
        return horarioDAO.findByNombrePlantilla(nombrePlantilla);
    }
}
