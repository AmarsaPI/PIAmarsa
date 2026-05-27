package com.adrian.almarsa.gestionfichajes.mvc.models.entity;

/**
 * Enumeración que representa el estado actual de una ausencia.
 *
 * Se utiliza para controlar el flujo de aprobación de solicitudes
 * y para mostrar el estado en interfaces de usuario o informes.
 */
public enum EstadoAusencia {

    /** La ausencia está pendiente de revisión. */
    PENDIENTE,

    /** La ausencia ha sido aprobada por un administrador. */
    APROBADA,

    /** La ausencia ha sido rechazada. */
    RECHAZADA
}
