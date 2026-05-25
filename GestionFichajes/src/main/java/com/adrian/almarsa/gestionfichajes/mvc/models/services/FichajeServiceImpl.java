package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
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
        Long empleadoId = fichaje.getEmpleado().getId();
        Optional<Fichaje> fichajeAbierto = fichajeDAO.findFirstByEmpleadoIdAndFechaSalidaIsNullOrderByIdDesc(empleadoId);

        if (fichajeAbierto.isPresent()) {
            Fichaje activo = fichajeAbierto.get();
            LocalDateTime ahora = LocalDateTime.now();
            
            // Comparamos si el fichaje abierto es de HOY o de AYER (o antes)
            boolean esDeHoy = activo.getFechaEntrada().toLocalDate().equals(ahora.toLocalDate());

            // Si es de hoy, bloqueamos porque es un fichaje en curso
            if (esDeHoy) {
                throw new RuntimeException("Ya tienes una jornada iniciada hoy. Por favor, regístrala antes de una nueva.");
            }
        
        }

        if (fichaje.getFechaEntrada() == null) fichaje.setFechaEntrada(java.time.LocalDateTime.now());
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
    
    @Override
    @Transactional(readOnly = true)
    public List<Fichaje> findFichajesConOlvido(Long empleadoId) {
        return fichajeDAO.findFichajesConOlvido(empleadoId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public double obtenerHorasTotalesPorEmpleadoYFecha(Long empleadoId, LocalDate fecha) {
        List<Fichaje> fichajesDelDia = fichajeDAO.findByEmpleadoIdAndFecha(empleadoId, fecha);
        
        if (fichajesDelDia == null || fichajesDelDia.isEmpty()) {
            return 0.0; // Si no hay fichajes, trabajó 0 horas
        }
        
        long minutosTotales = 0;
        
        // 2. Recorremos los fichajes del día sumando los intervalos de tiempo
        for (Fichaje f : fichajesDelDia) {
            if (f.getFechaEntrada() != null && f.getFechaSalida() != null) {
                long minutos = ChronoUnit.MINUTES.between(f.getFechaEntrada(), f.getFechaSalida());
                minutosTotales += minutos;
            }
        }
        
        // 3. Pasamos los minutos a horas decimales (ej: 45 min -> 0.75 horas)
        return minutosTotales / 60.0;
    }
    
    public List<Fichaje> findByEmpleadoAndMonth(Long empleadoId, String anioMes) {
        // Ejemplo lógico (ajusta según tu base de datos):
        // SELECT f FROM Fichaje f WHERE f.empleado.id = :id 
        // AND FUNCTION('DATE_FORMAT', f.fechaEntrada, '%Y-%m') = :anioMes
        return fichajeDAO.findByEmpleadoAndMonth(empleadoId, anioMes);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Fichaje> findByEmpleadoAndYear(Long empleadoId, int anio) {
        // Usando Spring Data JPA:
        return fichajeDAO.findByEmpleadoAndYear(empleadoId, anio);
    }
    
}