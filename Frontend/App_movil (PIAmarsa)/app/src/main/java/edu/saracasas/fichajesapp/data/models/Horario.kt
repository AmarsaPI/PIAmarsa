package edu.saracasas.fichajesapp.data.models

import java.time.DayOfWeek
import java.time.LocalTime

/**
 * Horario es una clase de datos que representa el horario de trabajo de un empleado.
 * Contiene información como el ID del horario, el día de la semana, la hora de entrada y salida,
 * y el empleado asociado a ese horario.
 */
data class Horario(
    val id: Long,
    val diaSemana: DayOfWeek,
    val horaEntrada: LocalTime,
    val horaSalida: LocalTime,
    val empleado: Empleado
)
