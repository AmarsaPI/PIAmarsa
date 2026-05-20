package com.adrian.almarsa.gestionfichajes.mvc.models.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "ausencias")
public class Ausencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado; 
    
    @NotNull(message = "La fecha inicio no puede estar vacía")
    @Column(nullable = false)
    private LocalDate fechaInicio;
    
    @NotNull(message = "La fecha fin no puede estar vacía")
    @Column(nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAusencia tipo; 
    
    @Enumerated(EnumType.STRING) // <- AÑADIR ESTO
    @Column(nullable = false)
    private EstadoAusencia estado = EstadoAusencia.PENDIENTE;
    
    @Column
    private String observaciones;

	public Ausencia() {}

	public Ausencia(Empleado empleado, LocalDate fechaInicio, LocalDate fechaFin, TipoAusencia tipo, String observaciones) {
		this.empleado = empleado;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.tipo = tipo;
		this.observaciones = observaciones;
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

	public TipoAusencia getTipo() {
		return tipo;
	}

	public void setTipo(TipoAusencia tipo) {
		this.tipo = tipo;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	
	public EstadoAusencia getEstado() { return estado; }
    public void setEstado(EstadoAusencia estado) { this.estado = estado; }
    
    public long getTotalDias() {
        if (this.fechaInicio != null && this.fechaFin != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(this.fechaInicio, this.fechaFin) + 1;
        }
        return 0;
    }
    
}
