package edu.saracasas.fichajesapp.data.models

import com.google.gson.annotations.SerializedName
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/**
 * Horario es una clase de datos que representa el horario de trabajo de un empleado.
 * Contiene información como el ID del horario, el día de la semana, la hora de entrada y salida,
 * y el empleado asociado a ese horario.
 */
data class Horario(
    @SerializedName("id") val id: Long,
    @SerializedName("fecha") val fecha: LocalDate,
    @SerializedName("horaInicio") val horaEntrada: LocalTime,
    @SerializedName("horaFin") val horaSalida: LocalTime,
    @SerializedName("empleado_id") val empleado: Empleado
)
