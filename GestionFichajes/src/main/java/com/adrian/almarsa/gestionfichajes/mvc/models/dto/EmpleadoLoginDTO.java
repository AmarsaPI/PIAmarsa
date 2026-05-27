package com.adrian.almarsa.gestionfichajes.mvc.models.dto;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;

/**
 * Objeto de Transferencia de Datos (DTO) para la autenticación de empleados.
 * <p>
 * Esta clase se utiliza para serializar la información básica de un empleado
 * tras un inicio de sesión exitoso, evitando la recursividad y los problemas
 * de serialización JSON asociados a las relaciones de entidad JPA (como calendarios).
 * </p>
 */
public class EmpleadoLoginDTO {

    /** Identificador único del empleado. */
    public Long id;

    /** Nombre completo del empleado. */
    public String nombre;

    /** Correo electrónico utilizado como identificador de acceso. */
    public String email;

    /** Rol asignado al empleado (ej. ADMINISTRADOR, EMPLEADO). */
    public String rol;
    
    /**
     * Construye un nuevo {@code EmpleadoLoginDTO} a partir de una entidad {@link Empleado}.
     * * @param e la entidad {@link Empleado} de la cual se extraerán los datos.
     */
    public EmpleadoLoginDTO(Empleado e) {
        this.id = e.getId();
        this.nombre = e.getNombre();
        this.email = e.getEmail();
        this.rol = e.getRol().toString();
    }
}
