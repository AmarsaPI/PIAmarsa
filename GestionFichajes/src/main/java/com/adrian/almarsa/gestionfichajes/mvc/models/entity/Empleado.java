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
import jakarta.validation.constraints.Size;

// Entidad principal que representa a los empleados en la base de datos
@Entity
@Table(name = "empleados")
public class Empleado implements Serializable, UserDetails { // Implementa UserDetails para integrarse con Spring Security

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotEmpty(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotEmpty(message = "El email no puede estar vacío")
    @Email(message = "Debe introducir un email válido")
    @Column(nullable = false, unique = true) // El email es el identificador único para el login
    private String email;
    
    @NotEmpty(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) // Guarda el nombre del rol (ADMINISTRADOR/EMPLEADO) como texto
    @Column(nullable = false)
    private Rol rol;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // Relación uno a muchos: un empleado puede tener muchos registros de fichajes
    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL)
    private List<Fichaje> fichajes;

    // Relación uno a muchos: un empleado tiene asignados varios días de horario
    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL)
    private List<Horario> horarios;

    // Se ejecuta automáticamente antes de insertar el registro en la DB
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // --- Métodos obligatorios de UserDetails para seguridad ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convierte el Rol en una autoridad entendible por Spring Security (prefijo ROLE_)
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.rol.name()));
    }

    @Override
    public String getUsername() {
        return this.email; // Usamos el email como nombre de usuario para el login
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }

    // --- Constructores, Getters y Setters ---

    public Empleado() {}

    public Empleado(String nombre, String email, String password, Rol rol) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
}