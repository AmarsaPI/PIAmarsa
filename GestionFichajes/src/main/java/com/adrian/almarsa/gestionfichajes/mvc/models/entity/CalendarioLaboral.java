package com.adrian.almarsa.gestionfichajes.mvc.models.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

// Entidad principal que representa un calendario laboral en la base de datos
@Entity
@Table(name = "calendarios_laborales")
public class CalendarioLaboral implements Serializable { // Implementa UserDetails para integrarse con Spring Security

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotEmpty(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotNull(message = "El año no puede estar vacío")
    @Column(nullable = false) 
    private Integer anyo;
    
    @OneToMany(mappedBy = "calendario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Festivo> festivos;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "calendario")
    private List<Empleado> empleados;

    public CalendarioLaboral() {}

    public CalendarioLaboral(String nombre, Integer anyo, List<Festivo> festivos) {
        this.nombre = nombre;
        this.anyo = anyo;
        this.festivos = festivos;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Integer getAnyo() {
		return anyo;
	}

	public void setAnyo(Integer anyo) {
		this.anyo = anyo;
	}

	public List<Festivo> getFestivos() {
		return festivos;
	}

	public void setFestivos(List<Festivo> festivos) {
		this.festivos = festivos;
	}

	public List<Empleado> getEmpleados() {
		return empleados;
	}

	public void setEmpleados(List<Empleado> empleados) {
		this.empleados = empleados;
	}
    
	
}
