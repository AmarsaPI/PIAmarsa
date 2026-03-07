package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.util.List;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Fichaje;

// Interfaz que gestiona el ciclo de vida de la jornada laboral
public interface IFichajeService {

    // Lista todos los registros de fichajes históricos
    List<Fichaje> findAll();

    // Obtiene un registro de fichaje por su identificador único
    Fichaje findById(Long id);

    // Elimina un registro (acción administrativa para corrección de errores)
    void delete(Long id);

    // Guarda cambios directos en un fichaje (edición administrativa)
    Fichaje save(Fichaje fichaje);

    // Inicia una jornada laboral validando que no haya otra abierta
    Fichaje registrarEntrada(Fichaje fichaje);

    // Finaliza una jornada laboral asignando la hora actual a la salida
    Fichaje registrarSalida(Long fichajeId);
    
    // Recupera todos los fichajes realizados por un empleado específico
    List<Fichaje> findByEmpleado(Long empleadoId);
}