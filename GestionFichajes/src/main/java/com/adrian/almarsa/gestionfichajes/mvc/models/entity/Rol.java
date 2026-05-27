package com.adrian.almarsa.gestionfichajes.mvc.models.entity;

/**
 * Roles disponibles para los usuarios del sistema.
 */
public enum Rol {

    /** Rol básico: gestiona solo sus propios fichajes y puede hacer solicitudes */
    EMPLEADO,

    /** Rol con acceso completo a la gestión del sistema. */
    ADMINISTRADOR
}
