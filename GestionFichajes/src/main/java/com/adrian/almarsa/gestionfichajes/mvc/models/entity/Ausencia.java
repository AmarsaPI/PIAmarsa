package com.adrian.almarsa.gestionfichajes.mvc.models.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Entidad que representa una ausencia registrada por un empleado.
 *
 * Una ausencia puede ser de distintos tipos (vacaciones, baja, permiso…)
 * y siempre contiene un rango de fechas, un estado y opcionalmente
 * observaciones del empleado o del administrador.
 *
 * Se utiliza en los procesos de solicitud, aprobación y control
 * de ausencias dentro del sistema.
 */
@Entity
@Table(name = "ausencias")
public class Ausencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Empleado al que pertenece la ausencia.
     */
    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    /**
     * Fecha de inicio de la ausencia.
     */
    @NotNull(message = "La fecha inicio no puede estar vacía")
    @Column(nullable = false)
    private LocalDate fechaInicio;

    /**
     * Fecha de fin de la ausencia.
     */
    @NotNull(message = "La fecha fin no puede estar vacía")
    @Column(nullable = false)
    private LocalDate fechaFin;

    /**
     * Tipo de ausencia (vacaciones, baja, permiso…).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAusencia tipo;

    /**
     * Estado actual de la ausencia (pendiente, aprobada, rechazada).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAusencia estado = EstadoAusencia.PENDIENTE;

    /**
     * Observaciones opcionales añadidas por el empleado o el administrador.
     */
    @Column
    private String observaciones;

    /**
     * Constructor vacío requerido por JPA.
     */
    public Ausencia() {}

    /**
     * Constructor principal para crear una ausencia.
     *
     * @param empleado empleado que solicita la ausencia
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @param tipo tipo de ausencia
     * @param observaciones texto opcional con detalles adicionales
     */
    public Ausencia(Empleado empleado, LocalDate fechaInicio, LocalDate fechaFin,
                    TipoAusencia tipo, String observaciones) {
        this.empleado = empleado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.tipo = tipo;
        this.observaciones = observaciones;
    }

    /** @return identificador único de la ausencia */
    public Long getId() { return id; }

    /** @param id nuevo identificador */
    public void setId(Long id) { this.id = id; }

    /** @return empleado al que pertenece la ausencia */
    public Empleado getEmpleado() { return empleado; }

    /** @param empleado empleado asociado a la ausencia */
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    /** @return fecha de inicio de la ausencia */
    public LocalDate getFechaInicio() { return fechaInicio; }

    /** @param fechaInicio nueva fecha de inicio */
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    /** @return fecha de fin de la ausencia */
    public LocalDate getFechaFin() { return fechaFin; }

    /** @param fechaFin nueva fecha de fin */
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    /** @return tipo de ausencia */
    public TipoAusencia getTipo() { return tipo; }

    /** @param tipo nuevo tipo de ausencia */
    public void setTipo(TipoAusencia tipo) { this.tipo = tipo; }

    /** @return observaciones adicionales */
    public String getObservaciones() { return observaciones; }

    /** @param observaciones nuevo texto de observaciones */
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    /** @return estado actual de la ausencia */
    public EstadoAusencia getEstado() { return estado; }

    /** @param estado nuevo estado de la ausencia */
    public void setEstado(EstadoAusencia estado) { this.estado = estado; }

    /**
     * Calcula el número total de días de la ausencia,
     * incluyendo ambos extremos del rango.
     *
     * @return total de días de ausencia o 0 si las fechas no son válidas
     */
    public long getTotalDias() {
        if (this.fechaInicio != null && this.fechaFin != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(this.fechaInicio, this.fechaFin) + 1;
        }
        return 0;
    }
}

