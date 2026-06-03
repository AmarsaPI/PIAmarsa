package edu.saracasas.fichajesapp.data.models

enum class EstadoAusencia {
    /** La ausencia está pendiente de revisión. */
    PENDIENTE,

    /** La ausencia ha sido aprobada por un administrador. */
    APROBADA,

    /** La ausencia ha sido rechazada. */
    RECHAZADA
}