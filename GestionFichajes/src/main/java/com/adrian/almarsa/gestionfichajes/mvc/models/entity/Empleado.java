package com.adrian.almarsa.gestionfichajes.mvc.models.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * Entidad que representa a un empleado dentro del sistema.
 *
 * Los empleados pueden autenticarse mediante Spring Security, por lo que
 * esta clase implementa {@link UserDetails}. El email actúa como nombre
 * de usuario y el rol determina los permisos dentro de la aplicación.
 *
 * Además, un empleado puede tener fichajes, contratos y un calendario laboral
 * asociado, lo que permite gestionar su jornada, ausencias y planificación.
 */
@Entity
@Table(name = "empleados")
public class Empleado implements Serializable, UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre completo del empleado.
     */
    @Column(nullable = false)
    @NotEmpty(message = "El nombre no puede estar vacío")
    private String nombre;

    /**
     * Email único del empleado, utilizado como identificador de login.
     */
    @NotEmpty(message = "El email no puede estar vacío")
    @Email(message = "Debe introducir un email válido")
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Contraseña cifrada del empleado.
     */
    @NotEmpty(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    private String password;

    /**
     * Rol del empleado dentro del sistema (ADMINISTRADOR o EMPLEADO).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    /**
     * Fecha de creación del registro.
     * Se establece automáticamente al insertar el empleado.
     */
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * Indica si el empleado está activo en el sistema.
     */
    @Column(nullable = false)
    private boolean activo = true;

    /**
     * Lista de fichajes realizados por el empleado.
     */
    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("empleado")
    private List<Fichaje> fichajes;

    /**
     * Calendario laboral asignado al empleado.
     */
    @ManyToOne
    @JoinColumn(name = "calendario_id")
    @JsonIgnoreProperties("empleados")
    private CalendarioLaboral calendario = new CalendarioLaboral();

    /**
     * Lista de contratos laborales del empleado.
     */
    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Contrato> contratos = new ArrayList<>();

    /**
     * Se ejecuta automáticamente antes de insertar el registro.
     * Registra la fecha de creación del empleado.
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    //Métodos seguridad
    /**
     * Convierte el rol del empleado en una autoridad entendible por Spring Security.
     *
     * @return colección con la autoridad ROLE_{ROL}
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.rol.name()));
    }

    /**
     * El email se utiliza como nombre de usuario para el login.
     *
     * @return email del empleado
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    /** @return siempre true, indicando que la cuenta no expira */
    @Override
    public boolean isAccountNonExpired() { return true; }

    /** @return siempre true, indicando que la cuenta no está bloqueada */
    @Override
    public boolean isAccountNonLocked() { return true; }

    /** @return siempre true, indicando que las credenciales no expiran */
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    /** @return siempre true, indicando que la cuenta está habilitada */
    @Override
    public boolean isEnabled() { return true; }

    /** Constructor vacío requerido por JPA. */
    public Empleado() {}

    /**
     * Constructor principal para crear empleados manualmente.
     *
     * @param nombre nombre del empleado
     * @param email email del empleado
     * @param password contraseña cifrada
     * @param rol rol del empleado
     */
    public Empleado(String nombre, String email, String password, Rol rol) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    /** @return identificador único del empleado */
    public Long getId() { return id; }

    /** @param id nuevo identificador */
    public void setId(Long id) { this.id = id; }

    /** @return nombre del empleado */
    public String getNombre() { return nombre; }

    /** @param nombre nuevo nombre del empleado */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /** @return email del empleado */
    public String getEmail() { return email; }

    /** @param email nuevo email del empleado */
    public void setEmail(String email) { this.email = email; }

    /** @return contraseña cifrada del empleado */
    public String getPassword() { return password; }

    /** @param password nueva contraseña cifrada */
    public void setPassword(String password) { this.password = password; }

    /** @return rol del empleado */
    public Rol getRol() { return rol; }

    /** @param rol nuevo rol del empleado */
    public void setRol(Rol rol) { this.rol = rol; }

    /** @return calendario laboral asignado */
    public CalendarioLaboral getCalendario() { return calendario; }

    /** @param calendario nuevo calendario laboral */
    public void setCalendario(CalendarioLaboral calendario) { this.calendario = calendario; }

    /** @return lista de contratos del empleado */
    public List<Contrato> getContratos() { return contratos; }

    /** @param contratos nueva lista de contratos */
    public void setContratos(List<Contrato> contratos) { this.contratos = contratos; }

    /** @return true si el empleado está activo */
    public boolean isActivo() { return activo; }

    /** @param activo nuevo estado de actividad */
    public void setActivo(boolean activo) { this.activo = activo; }
}
