package com.adrian.almarsa.gestionfichajes.mvc.models.entity;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.persistence.*;

/**
 * Plantilla de horario para un día concreto de la semana.
 */
@Entity
@Table(name = "plantillas_horarios")
public class PlantillaHorario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre opcional de la plantilla. */
    @Column(name = "nombre_plantilla", length = 100)
    private String nombrePlantilla;

    /** Día de la semana al que pertenece la plantilla. */
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DayOfWeek diaSemana;

    /** Hora de inicio prevista. */
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    /** Hora de fin prevista. */
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    /** Constructor vacío requerido por JPA. */
    public PlantillaHorario() {}

    /**
     * Crea una plantilla de horario.
     *
     * @param nombrePlantilla nombre opcional
     * @param diaSemana día de la semana
     * @param horaInicio inicio
     * @param horaFin fin
     */
    public PlantillaHorario(String nombrePlantilla, DayOfWeek diaSemana,
                            LocalTime horaInicio, LocalTime horaFin) {
        this.nombrePlantilla = nombrePlantilla;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    /** @return id de la plantilla */
    public Long getId() { return id; }

    /** @return nombre de la plantilla */
    public String getNombrePlantilla() { return nombrePlantilla; }

    /** @param nombrePlantilla nuevo nombre */
    public void setNombrePlantilla(String nombrePlantilla) { this.nombrePlantilla = nombrePlantilla; }

    /** @return día de la semana */
    public DayOfWeek getDiaSemana() { return diaSemana; }

    /** @param diaSemana nuevo día */
    public void setDiaSemana(DayOfWeek diaSemana) { this.diaSemana = diaSemana; }

    /** @return hora de inicio */
    public LocalTime getHoraInicio() { return horaInicio; }

    /** @param horaInicio nueva hora de inicio */
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    /** @return hora de fin */
    public LocalTime getHoraFin() { return horaFin; }

    /** @param horaFin nueva hora de fin */
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
}
