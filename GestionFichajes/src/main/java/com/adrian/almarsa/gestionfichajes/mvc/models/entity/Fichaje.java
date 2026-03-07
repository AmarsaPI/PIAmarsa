package com.adrian.almarsa.gestionfichajes.mvc.models.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;

// Entidad que registra la jornada laboral (entrada y salida) de los empleados
@Entity
@Table(name = "fichajes")
public class Fichaje implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Fecha y hora exacta del inicio de la jornada
    @Column(name = "fecha_entrada")
    private LocalDateTime fechaEntrada;

    // Fecha y hora del fin de jornada. Permanece en NULL mientras el empleado está trabajando
    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;

    // Relación muchos a uno: muchos fichajes pertenecen a un único empleado
    @ManyToOne(fetch = FetchType.LAZY) // LAZY mejora el rendimiento al no cargar el empleado si no es necesario
    @JoinColumn(name = "empleado_id", nullable = false) // Clave foránea en la base de datos
    private Empleado empleado;

    // Constructor vacío requerido por JPA
    public Fichaje() {}

    // Constructor para inicializar fichajes con datos específicos
    public Fichaje(LocalDateTime fechaEntrada, LocalDateTime fechaSalida, Empleado empleado) {
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.empleado = empleado;
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public LocalDateTime getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(LocalDateTime fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }
}