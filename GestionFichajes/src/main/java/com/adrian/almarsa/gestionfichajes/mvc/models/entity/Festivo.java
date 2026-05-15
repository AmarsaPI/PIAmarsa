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

@Entity
@Table(name = "festivos")
public class Festivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    @NotNull(message = "La fecha no puede estar vacía")
    private LocalDate fecha;
    
    @Column(nullable = true)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "calendario_id")
    private CalendarioLaboral calendario;  

	public Festivo() {
	}

	public Festivo(@NotEmpty(message = "La fecha no puede estar vacía") LocalDate fecha, String descripcion,
			CalendarioLaboral calendario) {
		super();
		this.fecha = fecha;
		this.descripcion = descripcion;
		this.calendario = calendario;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public CalendarioLaboral getCalendario() {
		return calendario;
	}

	public void setCalendario(CalendarioLaboral calendario) {
		this.calendario = calendario;
	}
    
}
