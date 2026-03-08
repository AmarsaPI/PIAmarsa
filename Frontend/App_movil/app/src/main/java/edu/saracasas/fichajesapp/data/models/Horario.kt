package edu.saracasas.fichajesapp.data.models

import java.time.DayOfWeek
import java.time.LocalTime

data class Horario(
    val id: Long,
    val diaSemana: DayOfWeek,
    val horaEntrada: LocalTime,
    val horaSalida: LocalTime,
    val empleado: Empleado
)
