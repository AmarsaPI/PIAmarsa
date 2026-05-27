package com.adrian.almarsa.gestionfichajes.mvc.models.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Horario asignado a un empleado para un día concreto.
 */
@Entity
@Table(name = "horarios")
public class Horario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Fecha del horario. */
    @Column(nullable = false)
    private LocalDate fecha;

    /** Hora de inicio del primer tramo. */
    @Column(nullable = false)
    private LocalTime horaInicio;

    /** Hora de fin del primer tramo. */
    @Column(nullable = false)
    private LocalTime horaFin;

    /** Hora de inicio del segundo tramo (opcional). */
    @Column(nullable = true)
    private LocalTime horaInicio2;

    /** Hora de fin del segundo tramo (opcional). */
    @Column(nullable = true)
    private LocalTime horaFin2;

    /** Tipo de día (laborable, festivo, vacaciones…). */
    @Column(name = "tipo_dia")
    private String tipo;

    /** Empleado al que pertenece el horario. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    /** Constructor vacío requerido por JPA. */
    public Horario() {}

    /**
     * Crea un horario.
     *
     * @param fecha fecha del horario
     * @param horaInicio inicio primer tramo
     * @param horaFin fin primer tramo
     * @param horaInicio2 inicio segundo tramo
     * @param horaFin2 fin segundo tramo
     * @param tipo tipo de día
     * @param empleado empleado asociado
     */
    public Horario(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                   LocalTime horaInicio2, LocalTime horaFin2,
                   String tipo, Empleado empleado) {
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.horaInicio2 = horaInicio2;
        this.horaFin2 = horaFin2;
        this.tipo = tipo;
        this.empleado = empleado;
    }

    /** @return id del horario */
    public Long getId() { return id; }

    /** @param id nuevo id */
    public void setId(Long id) { this.id = id; }

    /** @return fecha del horario */
    public LocalDate getFecha() { return fecha; }

    /** @param fecha nueva fecha */
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    /** @return hora de inicio del primer tramo */
    public LocalTime getHoraInicio() { return horaInicio; }

    /** @param horaInicio nueva hora de inicio */
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    /** @return hora de fin del primer tramo */
    public LocalTime getHoraFin() { return horaFin; }

    /** @param horaFin nueva hora de fin */
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    /** @return hora de inicio del segundo tramo */
    public LocalTime getHoraInicio2() { return horaInicio2; }

    /** @param horaInicio2 nueva hora de inicio del segundo tramo */
    public void setHoraInicio2(LocalTime horaInicio2) { this.horaInicio2 = horaInicio2; }

    /** @return hora de fin del segundo tramo */
    public LocalTime getHoraFin2() { return horaFin2; }

    /** @param horaFin2 nueva hora de fin del segundo tramo */
    public void setHoraFin2(LocalTime horaFin2) { this.horaFin2 = horaFin2; }

    /** @return tipo de día */
    public String getTipo() { return tipo; }

    /** @param tipo nuevo tipo de día */
    public void setTipo(String tipo) { this.tipo = tipo; }

    /** @return empleado asociado */
    public Empleado getEmpleado() { return empleado; }

    /** @param empleado nuevo empleado asociado */
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
}
