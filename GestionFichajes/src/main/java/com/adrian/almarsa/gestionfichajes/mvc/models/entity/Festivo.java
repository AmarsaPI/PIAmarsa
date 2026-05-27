package com.adrian.almarsa.gestionfichajes.mvc.models.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Representa un día festivo dentro de un calendario laboral.
 */
@Entity
@Table(name = "festivos")
public class Festivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Fecha del festivo. */
    @Column(nullable = false)
    @NotNull(message = "La fecha no puede estar vacía")
    private LocalDate fecha;

    /** Descripción opcional del festivo. */
    @Column(nullable = true)
    private String descripcion;

    /** Calendario al que pertenece el festivo. */
    @ManyToOne
    @JoinColumn(name = "calendario_id")
    private CalendarioLaboral calendario;

    /** Constructor vacío requerido por JPA. */
    public Festivo() {}

    /**
     * Crea un festivo.
     *
     * @param fecha fecha del festivo
     * @param descripcion descripción opcional
     * @param calendario calendario asociado
     */
    public Festivo(LocalDate fecha, String descripcion, CalendarioLaboral calendario) {
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.calendario = calendario;
    }

    /** @return id del festivo */
    public Long getId() { return id; }

    /** @param id nuevo id */
    public void setId(Long id) { this.id = id; }

    /** @return fecha del festivo */
    public LocalDate getFecha() { return fecha; }

    /** @param fecha nueva fecha */
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    /** @return descripción del festivo */
    public String getDescripcion() { return descripcion; }

    /** @param descripcion nueva descripción */
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    /** @return calendario asociado */
    public CalendarioLaboral getCalendario() { return calendario; }

    /** @param calendario nuevo calendario */
    public void setCalendario(CalendarioLaboral calendario) { this.calendario = calendario; }
}
