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

// Entidad principal que representa a los admin en la base de datos
@Entity
@Table(name = "administradores")
public class Admin implements Serializable, UserDetails { // Implementa UserDetails para integrarse con Spring Security

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

    @Column(updatable = false)
    private LocalDateTime createdAt;

    // Se ejecuta automáticamente antes de insertar el registro en la DB
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // --- Métodos obligatorios de UserDetails para seguridad ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Le damos un rol fijo de ADMIN
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
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

    public Admin() {}

    public Admin(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
}
