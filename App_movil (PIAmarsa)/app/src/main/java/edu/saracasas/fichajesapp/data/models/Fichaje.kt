package edu.saracasas.fichajesapp.data.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

/**
 * Fichaje es una clase de datos que representa un registro de fichaje en el sistema.
 * Contiene información como el ID del fichaje, fecha y hora de entrada y salida, el empleado asociado,
 * la fecha y hora en formato String, y el tipo de fichaje (entrada o salida).
 */
data class Fichaje(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("fechaEntrada") val fechaEntrada: LocalDateTime? = null,
    @SerializedName("fechaSalida") val fechaSalida: LocalDateTime? = null,
    @SerializedName("empleado") val empleado: Empleado
)