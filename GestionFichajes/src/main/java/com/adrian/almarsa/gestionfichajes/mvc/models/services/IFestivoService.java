package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.util.List;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Festivo;

public interface IFestivoService {

    // Para el administrador: Ver todos los festivos (opcional)
    public List<Festivo> findAll();

    // Para el calendario: Obtener los festivos de un calendario específico
    public List<Festivo> findByCalendario(Long calendarioId);

    // Para el calendario: Obtener festivos directamente por el ID del empleado
    // (Navegando: Empleado -> Calendario -> Festivos)
    public List<Festivo> findByEmpleado(Long empleadoId);

    // Guardar o actualizar un festivo
    public Festivo save(Festivo festivo);

    // Buscar un festivo por su ID
    public Festivo findById(Long id);

    // Eliminar un festivo
    public void delete(Long id);
}