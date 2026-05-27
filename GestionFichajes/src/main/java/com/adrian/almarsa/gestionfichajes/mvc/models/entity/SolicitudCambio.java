package com.adrian.almarsa.gestionfichajes.mvc.models.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.persistence.*;

/**
 * Solicitud de modificación de un fichaje o turno.
 */
@Entity
@Table(name = "solicitudes_cambio")
public class SolicitudCambio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Fichaje asociado a la solicitud (opcional). */
    @ManyToOne
    @JoinColumn(name = "fichaje_id")
    private Fichaje fichaje;

    /** Hora de entrada propuesta. */
    private LocalTime horaEntradaPropuesta;

    /** Hora de salida propuesta. */
    private LocalTime horaSalidaPropuesta;

    /** Hora de entrada del segundo tramo propuesta. */
    private LocalTime horaEntradaPropuesta2;

    /** Hora de salida del segundo tramo propuesta. */
    private LocalTime horaSalidaPropuesta2;

    /** Fecha en la que se realiza la solicitud. */
    private LocalDate fechaSolicitud;

    /** Motivo de la solicitud. */
    private String motivo;

    /** Fecha del turno afectado. */
    @Column(name = "fecha_turno")
    private LocalDate fechaTurno;

    /** Estado de la solicitud. */
    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    /** Empleado que realiza la solicitud. */
    @ManyToOne
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;

    /** Constructor vacío requerido por JPA. */
    public SolicitudCambio() {}

    /**
     * Crea una solicitud de cambio.
     */
    public SolicitudCambio(Fichaje fichaje, LocalTime horaEntradaPropuesta, LocalTime horaSalidaPropuesta,
                           LocalTime horaEntradaPropuesta2, LocalTime horaSalidaPropuesta2,
                           LocalDate fechaSolicitud, String motivo, EstadoSolicitud estado,
                           LocalDate fechaTurno, Empleado empleado) {
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

    /** @return id de la solicitud */
    public Long getId() { return id; }

    /** @param id nuevo id */
    public void setId(Long id) { this.id = id; }

    /** @return fichaje asociado */
    public Fichaje getFichaje() { return fichaje; }

    /** @param fichaje nuevo fichaje */
    public void setFichaje(Fichaje fichaje) { this.fichaje = fichaje; }

    /** @return hora de entrada propuesta */
    public LocalTime getHoraEntradaPropuesta() { return horaEntradaPropuesta; }

    /** @param horaEntradaPropuesta nueva hora */
    public void setHoraEntradaPropuesta(LocalTime horaEntradaPropuesta) { this.horaEntradaPropuesta = horaEntradaPropuesta; }

    /** @return hora de salida propuesta */
    public LocalTime getHoraSalidaPropuesta() { return horaSalidaPropuesta; }

    /** @param horaSalidaPropuesta nueva hora */
    public void setHoraSalidaPropuesta(LocalTime horaSalidaPropuesta) { this.horaSalidaPropuesta = horaSalidaPropuesta; }

    /** @return hora de entrada del segundo tramo */
    public LocalTime getHoraEntradaPropuesta2() { return horaEntradaPropuesta2; }

    /** @param horaEntradaPropuesta2 nueva hora */
    public void setHoraEntradaPropuesta2(LocalTime horaEntradaPropuesta2) { this.horaEntradaPropuesta2 = horaEntradaPropuesta2; }

    /** @return hora de salida del segundo tramo */
    public LocalTime getHoraSalidaPropuesta2() { return horaSalidaPropuesta2; }

    /** @param horaSalidaPropuesta2 nueva hora */
    public void setHoraSalidaPropuesta2(LocalTime horaSalidaPropuesta2) { this.horaSalidaPropuesta2 = horaSalidaPropuesta2; }

    /** @return fecha de solicitud */
    public LocalDate getFechaSolicitud() { return fechaSolicitud; }

    /** @param fechaSolicitud nueva fecha */
    public void setFechaSolicitud(LocalDate fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    /** @return motivo */
    public String getMotivo() { return motivo; }

    /** @param motivo nuevo motivo */
    public void setMotivo(String motivo) { this.motivo = motivo; }

    /** @return estado de la solicitud */
    public EstadoSolicitud getEstado() { return estado; }

    /** @param estado nuevo estado */
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }

    /** @return fecha del turno afectado */
    public LocalDate getFechaTurno() { return fechaTurno; }

    /** @param fechaTurno nueva fecha */
    public void setFechaTurno(LocalDate fechaTurno) { this.fechaTurno = fechaTurno; }

    /** @return empleado solicitante */
    public Empleado getEmpleado() { return empleado; }

    /** @param empleado nuevo empleado */
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
}
