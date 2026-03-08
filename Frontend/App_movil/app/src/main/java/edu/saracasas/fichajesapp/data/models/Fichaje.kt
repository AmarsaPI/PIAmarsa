package edu.saracasas.fichajesapp.data.models

import java.time.LocalDateTime

data class Fichaje(
    val id: Long,
    val fechaEntrada: LocalDateTime,
    val fechaSalida: LocalDateTime,
    val empleado: Empleado,
    val fechaHora: String,
    val tipo: String
)
