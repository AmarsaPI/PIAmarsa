package com.adrian.almarsa.gestionfichajes.mvc.models.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.persistence.*;

@Entity
@Table(name = "solicitudes_cambio")
public class SolicitudCambio {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fichaje_id", nullable = true)
    private Fichaje fichaje;

    private LocalTime horaEntradaPropuesta;
    private LocalTime horaSalidaPropuesta;
    private LocalTime horaEntradaPropuesta2;
    private LocalTime horaSalidaPropuesta2;
    private LocalDate fechaSolicitud;
    private String motivo;
    
    @Column(name = "fecha_turno")
    private LocalDate fechaTurno;

	@Enumerated(EnumType.STRING)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;
	
	@ManyToOne
	@JoinColumn(name = "empleado_id", nullable = true)
	private Empleado empleado;

	public Empleado getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	public Long getId() {
		return id;
	}
	
	public LocalDate getFechaTurno() {
		return fechaTurno;
	}

	public void setFechaTurno(LocalDate fechaTurno) {
		this.fechaTurno = fechaTurno;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Fichaje getFichaje() {
		return fichaje;
	}

	public void setFichaje(Fichaje fichaje) {
		this.fichaje = fichaje;
	}

	public LocalTime getHoraEntradaPropuesta() {
		return horaEntradaPropuesta;
	}

	public void setHoraEntradaPropuesta(LocalTime horaEntradaPropuesta) {
		this.horaEntradaPropuesta = horaEntradaPropuesta;
	}

	public LocalTime getHoraSalidaPropuesta() {
		return horaSalidaPropuesta;
	}

	public void setHoraSalidaPropuesta(LocalTime horaSalidaPropuesta) {
		this.horaSalidaPropuesta = horaSalidaPropuesta;
	}
	
	public LocalTime getHoraEntradaPropuesta2() {
		return horaEntradaPropuesta2;
	}

	public void setHoraEntradaPropuesta2(LocalTime horaEntradaPropuesta2) {
		this.horaEntradaPropuesta2 = horaEntradaPropuesta2;
	}

	public LocalTime getHoraSalidaPropuesta2() {
		return horaSalidaPropuesta2;
	}

	public void setHoraSalidaPropuesta2(LocalTime horaSalidaPropuesta2) {
		this.horaSalidaPropuesta2 = horaSalidaPropuesta2;
	}

	public LocalDate getFechaSolicitud() {
		return fechaSolicitud;
	}

	public void setFechaSolicitud(LocalDate fechaSolicitud) {
		this.fechaSolicitud = fechaSolicitud;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public EstadoSolicitud getEstado() {
		return estado;
	}

	public void setEstado(EstadoSolicitud estado) {
		this.estado = estado;
	}

    public SolicitudCambio() {}

	public SolicitudCambio(Fichaje fichaje, LocalTime horaEntradaPropuesta, LocalTime horaSalidaPropuesta, LocalTime horaEntradaPropuesta2, LocalTime horaSalidaPropuesta2,
			LocalDate fechaSolicitud, String motivo, EstadoSolicitud estado, LocalDate fechaTurno, Empleado empleado) {
		this.fichaje = fichaje;
		this.horaEntradaPropuesta = horaEntradaPropuesta;
		this.horaSalidaPropuesta = horaSalidaPropuesta;
		this.horaEntradaPropuesta2 = horaEntradaPropuesta2;
		this.horaSalidaPropuesta2 = horaSalidaPropuesta2;
		this.fechaSolicitud = fechaSolicitud;
		this.motivo = motivo;
		this.estado = estado;
		this.fechaTurno = fechaTurno;
		this.empleado = empleado;
	}
    
    
}