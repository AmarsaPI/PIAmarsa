package com.adrian.almarsa.gestionfichajes.mvc.models.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;

/**
 * Registro de entrada y salida de un empleado.
 */
@Entity
@Table(name = "fichajes")
public class Fichaje implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Fecha y hora de entrada. */
    @Column(name = "fecha_entrada")
    private LocalDateTime fechaEntrada;

    /** Fecha y hora de salida (puede ser null mientras está trabajando). */
    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida = LocalDateTime.MIN;

    /** Empleado que realiza el fichaje. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    /** Constructor vacío requerido por JPA. */
    public Fichaje() {}

    /**
     * Crea un fichaje.
     *
     * @param fechaEntrada entrada
     * @param fechaSalida salida
     * @param empleado empleado asociado
     */
    public Fichaje(LocalDateTime fechaEntrada, LocalDateTime fechaSalida, Empleado empleado) {
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.empleado = empleado;
    }

    /** @return id del fichaje */
    public Long getId() { return id; }

    /** @return fecha de entrada */
    public LocalDateTime getFechaEntrada() { return fechaEntrada; }

    /** @param fechaEntrada nueva fecha de entrada */
    public void setFechaEntrada(LocalDateTime fechaEntrada) { this.fechaEntrada = fechaEntrada; }

    /** @return fecha de salida */
    public LocalDateTime getFechaSalida() { return fechaSalida; }

    /** @param fechaSalida nueva fecha de salida */
    public void setFechaSalida(LocalDateTime fechaSalida) { this.fechaSalida = fechaSalida; }

    /** @return empleado asociado */
    public Empleado getEmpleado() { return empleado; }

    /** @param empleado nuevo empleado asociado */
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
}
