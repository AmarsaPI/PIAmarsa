package com.adrian.almarsa.gestionfichajes.mvc.models.entity;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.persistence.*;

// Entidad que define las plantillas de horario de un día concreto
@Entity
@Table(name = "plantillas_horarios")
public class PlantillaHorario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre_plantilla", nullable = true, length = 100)
    private String nombrePlantilla;

    // Día de la semana (MONDAY, TUESDAY, etc.) almacenado como String en la DB
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DayOfWeek diaSemana;

    // Hora de entrada prevista (ej. 09:00)
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    // Hora de salida prevista (ej. 14:00)
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    // Constructor por defecto necesario para JPA
    public PlantillaHorario() {}

    // Constructor completo para facilitar la creación de instancias
    public PlantillaHorario(String nombrePlantilla, DayOfWeek diaSemana, LocalTime horaInicio, LocalTime horaFin) {
    	this.nombrePlantilla = nombrePlantilla;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    // --- Getters y Setters ---

    public Long getId() { return id; }
    
    public DayOfWeek getDiaSemana() { return diaSemana; }
    public void setDiaSemana(DayOfWeek diaSemana) { this.diaSemana = diaSemana; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
    
    public String getNombrePlantilla() { return nombrePlantilla; }
    public void setNombrePlantilla(String nombrePlantilla) { this.nombrePlantilla = nombrePlantilla; }
}