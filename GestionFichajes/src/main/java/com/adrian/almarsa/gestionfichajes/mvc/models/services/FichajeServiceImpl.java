package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IFichajeDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Fichaje;

@Service
public class FichajeServiceImpl implements IFichajeService {

    @Autowired
    private IFichajeDAO fichajeDAO;

    // Recupera todos los registros de la base de datos
    @Override
    @Transactional(readOnly = true)
    public List<Fichaje> findAll() {
        return (List<Fichaje>) fichajeDAO.findAll();
    }

    // Busca un fichaje por ID o devuelve null si no existe
    @Override
    @Transactional(readOnly = true)
    public Fichaje findById(Long id) {
        return fichajeDAO.findById(id).orElse(null);
    }

    // Elimina físicamente el registro de la jornada
    @Override
    @Transactional
    public void delete(Long id) {
        fichajeDAO.deleteById(id);
    }

    // Lógica para iniciar jornada: valida que no haya una entrada previa sin cerrar
    @Override
    @Transactional
    public Fichaje registrarEntrada(Fichaje fichaje) {
        // 1. Verificación de seguridad: ¿Viene el empleado?
        if (fichaje.getEmpleado() == null || fichaje.getEmpleado().getId() == null) {
            throw new RuntimeException("Error: Debe especificar un empleado válido para fichar.");
        }

        Long empleadoId = fichaje.getEmpleado().getId();

        // 2. Usamos el método correcto del DAO que definimos antes
        Optional<Fichaje> fichajeActivo = fichajeDAO.findFirstByEmpleadoIdAndFechaSalidaIsNullOrderByIdDesc(empleadoId);

        if (fichajeActivo.isPresent()) {
            throw new RuntimeException("Ya existe una jornada activa para este empleado. Debe cerrar la anterior.");
        }

        // 3. Si la fecha viene vacía (desde la web), le ponemos la actual
        if (fichaje.getFechaEntrada() == null) {
            fichaje.setFechaEntrada(java.time.LocalDateTime.now());
        }
        
        // Aseguramos que la salida sea NULL al empezar
        fichaje.setFechaSalida(null);

        return fichajeDAO.save(fichaje);
    }

    // Lógica para finalizar jornada: busca el registro y le asigna la hora actual de salida
    @Override
    @Transactional
    public Fichaje registrarSalida(Long fichajeId) {
        Fichaje fichaje = fichajeDAO.findById(fichajeId)
                .orElseThrow(() -> new RuntimeException("Fichaje no encontrado"));

        if (fichaje.getFechaSalida() != null) {
            throw new RuntimeException("El fichaje ya tiene salida registrada");
        }

        fichaje.setFechaSalida(java.time.LocalDateTime.now());
        return fichajeDAO.save(fichaje);
    }
    
    // Método genérico para guardar o actualizar (usado por Admin para correcciones)
    @Override
    @Transactional
    public Fichaje save(Fichaje fichaje) {
        return fichajeDAO.save(fichaje);
    }
    
    // Lista el historial de fichajes de un empleado usando el método corregido del DAO
    @Override
    @Transactional(readOnly = true)
    public List<Fichaje> findByEmpleado(Long empleadoId) {
        return fichajeDAO.findByEmpleado_Id(empleadoId);
    }

    // Lista el historial de fichajes de un empleado usando el método corregido del DAO
    @Transactional(readOnly = true)
    public List<Fichaje> findByEmpleadoSemanaActual(Long empleadoId) {
        LocalDateTime monday = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusDays(1);
        LocalDateTime sunday = LocalDateTime.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).plusDays(1);
        System.out.println(monday);
        System.out.println(sunday);
        return fichajeDAO.findByEmpleado_Id(empleadoId).stream().filter(
                fichaje -> fichaje.getFechaEntrada().isAfter(monday) && fichaje.getFechaEntrada().isBefore(sunday)).toList();
    }

    //Devuelve el último fichaje sin salida
    @Override
    @Transactional(readOnly = true)
    public Fichaje findUltimoSinCerrar(Long empleadoId) {
        return fichajeDAO.findFirstByEmpleadoIdAndFechaSalidaIsNullOrderByIdDesc(empleadoId)
                         .orElse(null); // Si no hay nada abierto, devuelve null
    }
}