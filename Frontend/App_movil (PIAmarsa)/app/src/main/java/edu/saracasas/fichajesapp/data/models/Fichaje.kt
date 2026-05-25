package edu.saracasas.fichajesapp.data.models

import java.time.LocalDateTime

/**
 * Fichaje es una clase de datos que representa un registro de fichaje en el sistema.
 * Contiene información como el ID del fichaje, fecha y hora de entrada y salida, el empleado asociado,
 * la fecha y hora en formato String, y el tipo de fichaje (entrada o salida).
 */
data class Fichaje(
    val id: Long? = null,
    val fechaEntrada: LocalDateTime? = null,
    val fechaSalida: LocalDateTime? = null,
    val empleado: Empleado,
)
