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

/**
 * Entidad que representa a un administrador del sistema.
 *
 * Los administradores se autentican mediante Spring Security, por lo que
 * esta clase implementa {@link UserDetails}. El email actúa como nombre
 * de usuario y se asigna un rol fijo de administrador.
 *
 * También almacena información básica como nombre, contraseña y fecha
 * de creación del registro.
 */
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

    /**
     * Se ejecuta automáticamente antes de insertar el registro.
     * Registra la fecha de creación del administrador.
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
    
    
    //Métodos necesarios para la capa de seguridad
    /**
     * Devuelve el rol del administrador.
     * En este sistema todos los administradores tienen un único rol fijo.
     *
     * @return colección con el rol ROLE_ADMIN
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Le damos un rol fijo de ADMIN
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
    
    /**
     * El email se utiliza como nombre de usuario para el login.
     *
     * @return email del administrador
     */
    @Override
    public String getUsername() {
        return this.email; // Usamos el email como nombre de usuario para el login
    }

    /**
     * Indica si la cuenta del administrador ha expirado.
     * En este sistema todas las cuentas están siempre activas.
     *
     * @return true siempre, indicando que la cuenta no expira
     */
    @Override
    public boolean isAccountNonExpired() { return true; }

    /**
     * Indica si la cuenta está bloqueada.
     * Los administradores no se bloquean automáticamente.
     *
     * @return true siempre, indicando que la cuenta no está bloqueada
     */
    @Override
    public boolean isAccountNonLocked() { return true; }

    /**
     * Indica si las credenciales (contraseña) han expirado.
     * En este sistema no se gestiona expiración de contraseñas.
     *
     * @return true siempre, indicando que las credenciales son válidas
     */
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    /**
     * Indica si la cuenta está habilitada.
     * Los administradores están siempre habilitados salvo que se implemente
     * lógica adicional en el futuro.
     *
     * @return true siempre, indicando que la cuenta está activa
     */
    @Override
    public boolean isEnabled() { return true; }

    public Admin() {}

    public Admin(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }
    
    /**
     * @return identificador único del administrador
     */
    public Long getId() { return id; }
    
    /**
     * @return nombre del administrador
     */
    public String getNombre() { return nombre; }

    /**
     * Establece el nombre del administrador.
     *
     * @param nombre nuevo nombre
     */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * @return email del administrador (también usado como username)
     */
    public String getEmail() { return email; }

    /**
     * Establece el email del administrador.
     *
     * @param email nuevo email
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * @return contraseña cifrada del administrador
     */
    public String getPassword() { return password; }

    /**
     * Establece la contraseña del administrador.
     *
     * @param password nueva contraseña cifrada
     */
    public void setPassword(String password) { this.password = password; }
}
