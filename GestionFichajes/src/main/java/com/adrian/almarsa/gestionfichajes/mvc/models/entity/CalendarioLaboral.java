package com.adrian.almarsa.gestionfichajes.mvc.models.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Entidad que representa un calendario laboral anual.
 *
 * Un calendario laboral agrupa los festivos oficiales o internos de la empresa
 * para un año concreto, y puede estar asociado a varios empleados.
 *
 * Se utiliza para determinar qué días son laborables o festivos en los
 * cálculos de fichajes, horarios y ausencias.
 */
@Entity
@Table(name = "calendarios_laborales")
public class CalendarioLaboral implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del calendario laboral (ej: "Calendario 2025", "General Empresa").
     */
    @Column(nullable = false)
    @NotEmpty(message = "El nombre no puede estar vacío")
    private String nombre;

    /**
     * Año al que pertenece el calendario laboral.
     */
    @NotNull(message = "El año no puede estar vacío")
    @Column(nullable = false)
    private Integer anyo;

    /**
     * Lista de festivos incluidos en este calendario.
     * Se eliminan automáticamente si se elimina el calendario.
     */
    @OneToMany(mappedBy = "calendario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("calendario")
    private List<Festivo> festivos;

    /**
     * Fecha de creación del registro.
     * Se establece automáticamente al insertar el calendario.
     */
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * Empleados que están asociados a este calendario laboral.
     */
    @OneToMany(mappedBy = "calendario")
    @JsonIgnoreProperties("calendario")
    private List<Empleado> empleados;

    /**
     * Constructor vacío requerido por JPA.
     */
    public CalendarioLaboral() { }

    /**
     * Constructor principal para crear un calendario laboral.
     *
     * @param nombre nombre del calendario
     * @param anyo año del calendario
     * @param festivos lista de festivos asociados
     */
    public CalendarioLaboral(String nombre, Integer anyo, List<Festivo> festivos, List<Empleado> empleados) {
        this.nombre = nombre;
        this.anyo = anyo;
        this.festivos = festivos;
        this.empleados = empleados;
    }

    /** @return identificador único del calendario */
    public Long getId() { return id; }

    /** @param id nuevo identificador */
    public void setId(Long id) { this.id = id; }

    /** @return nombre del calendario laboral */
    public String getNombre() { return nombre; }

    /** @param nombre nuevo nombre del calendario */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /** @return año del calendario laboral */
    public Integer getAnyo() { return anyo; }

    /** @param anyo nuevo año del calendario */
    public void setAnyo(Integer anyo) { this.anyo = anyo; }

    /** @return lista de festivos del calendario */
    public List<Festivo> getFestivos() { return festivos; }

    /** @param festivos nueva lista de festivos */
    public void setFestivos(List<Festivo> festivos) { this.festivos = festivos; }

    /** @return lista de empleados asociados al calendario */
    public List<Empleado> getEmpleados() { return empleados; }

    /** @param empleados nueva lista de empleados asociados */
    public void setEmpleados(List<Empleado> empleados) { this.empleados = empleados; }
}

