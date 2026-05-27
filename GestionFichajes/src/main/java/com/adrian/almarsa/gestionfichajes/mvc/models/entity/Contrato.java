package com.adrian.almarsa.gestionfichajes.mvc.models.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

/**
 * Entidad que representa el contrato laboral de un empleado.
 *
 * Un contrato define el periodo de vigencia, las horas semanales pactadas
 * y los minutos teóricos diarios que se utilizarán para cálculos de
 * planificación, fichajes y balances de horas.
 *
 * Un empleado puede tener varios contratos a lo largo del tiempo,
 * pero solo uno activo en una fecha concreta.
 */
@Entity
@Table(name = "contratos")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Empleado al que pertenece este contrato.
     */
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Empleado empleado;

    /**
     * Fecha en la que comienza la vigencia del contrato.
     */
    @Column(nullable = false)
    private LocalDate fechaInicio;

    /**
     * Fecha en la que finaliza el contrato.
     * Puede ser null si el contrato sigue vigente.
     */
    private LocalDate fechaFin;

    /**
     * Total de horas semanales pactadas en el contrato.
     */
    @NotNull(message = "Las horas semanales no pueden ser nulas")
    @Min(value = 1, message = "Las horas semanales deben ser al menos 1")
    @Column(nullable = false)
    private int horasSemanalesTotales;

    /**
     * Minutos teóricos que el empleado debe trabajar cada día.
     * Se utiliza para cálculos de planificación y balances.
     */
    @Column(nullable = false)
    private int minutosTeoricosDiarios;

    /**
     * Constructor vacío requerido por JPA.
     */
    public Contrato() {}

    /**
     * Constructor principal para crear un contrato.
     *
     * @param empleado empleado asociado
     * @param fechaInicio fecha de inicio del contrato
     * @param fechaFin fecha de fin del contrato (puede ser null)
     * @param horasSemanalesTotales horas semanales pactadas
     * @param minutosTeoricosDiarios minutos teóricos diarios
     */
    public Contrato(Empleado empleado, LocalDate fechaInicio, LocalDate fechaFin,
                    int horasSemanalesTotales, int minutosTeoricosDiarios) {
        this.empleado = empleado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.horasSemanalesTotales = horasSemanalesTotales;
        this.minutosTeoricosDiarios = minutosTeoricosDiarios;
    }

    /** @return identificador único del contrato */
    public Long getId() { return id; }

    /** @param id nuevo identificador del contrato */
    public void setId(Long id) { this.id = id; }

    /** @return empleado asociado al contrato */
    public Empleado getEmpleado() { return empleado; }

    /** @param empleado nuevo empleado asociado */
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    /** @return fecha de inicio del contrato */
    public LocalDate getFechaInicio() { return fechaInicio; }

    /** @param fechaInicio nueva fecha de inicio */
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    /** @return fecha de fin del contrato (puede ser null) */
    public LocalDate getFechaFin() { return fechaFin; }

    /** @param fechaFin nueva fecha de fin */
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    /** @return horas semanales pactadas */
    public int getHorasSemanalesTotales() { return horasSemanalesTotales; }

    /** @param horasSemanalesTotales nuevas horas semanales pactadas */
    public void setHorasSemanalesTotales(int horasSemanalesTotales) {
        this.horasSemanalesTotales = horasSemanalesTotales;
    }

    /** @return minutos teóricos diarios */
    public int getMinutosTeoricosDiarios() { return minutosTeoricosDiarios; }

    /** @param minutosTeoricosDiarios nuevos minutos teóricos diarios */
    public void setMinutosTeoricosDiarios(int minutosTeoricosDiarios) {
        this.minutosTeoricosDiarios = minutosTeoricosDiarios;
    }
}
