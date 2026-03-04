package com.adrian.almarsa.gestionfichajes.mvc.models.entity;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.persistence.*;

// Entidad que define los turnos de trabajo teóricos asignados a cada empleado
@Entity
@Table(name = "horarios")
public class Horario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    // Relación muchos a uno: Varios días de horario pertenecen a un solo empleado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    // Constructor por defecto necesario para JPA
    public Horario() {}

    // Constructor completo para facilitar la creación de instancias
    public Horario(DayOfWeek diaSemana, LocalTime horaInicio, LocalTime horaFin, Empleado empleado) {
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.empleado = empleado;
    }

    // --- Getters y Setters ---

    public Long getId() { return id; }
    
    public DayOfWeek getDiaSemana() { return diaSemana; }
    public void setDiaSemana(DayOfWeek diaSemana) { this.diaSemana = diaSemana; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
}