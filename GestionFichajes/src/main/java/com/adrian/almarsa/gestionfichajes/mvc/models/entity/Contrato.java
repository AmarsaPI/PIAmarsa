package com.adrian.almarsa.gestionfichajes.mvc.models.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

@Entity
@Table(name = "contratos")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Empleado empleado; 

    @Column(nullable = false)
    private LocalDate fechaInicio;

    private LocalDate fechaFin; 
    
    @NotNull(message = "Las horas semanales no pueden ser nulas")
    @Min(value = 1, message = "Las horas semanales deben ser al menos 1")
    @Column(nullable = false)
    private int horasSemanalesTotales; 

    @Column(nullable = false)
    private int minutosTeoricosDiarios;

    // Constructor vacío obligatorio para JPA
    public Contrato() {}

    // Constructor para crear nuevos registros
	public Contrato(Empleado empleado, LocalDate fechaInicio, LocalDate fechaFin,
			int horasSemanalesTotales, int minutosTeoricosDiarios) {
		this.empleado = empleado;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.horasSemanalesTotales = horasSemanalesTotales;
		this.minutosTeoricosDiarios = minutosTeoricosDiarios;
	}
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Empleado getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDate getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}

	public int getHorasSemanalesTotales() {
		return horasSemanalesTotales;
	}

	public void setHorasSemanalesTotales(int horasSemanalesTotales) {
		this.horasSemanalesTotales = horasSemanalesTotales;
	}

	public int getMinutosTeoricosDiarios() {
		return minutosTeoricosDiarios;
	}

	public void setMinutosTeoricosDiarios(int minutosTeoricosDiarios) {
		this.minutosTeoricosDiarios = minutosTeoricosDiarios;
	}
}