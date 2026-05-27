package edu.saracasas.fichajesapp.data.models

data class HorarioResponse(
    val id: String,
    val title: String,
    val start: String, // 👈 Este es el campo "2026-05-27" que quieres como Key
    val allDay: Boolean,
    val backgroundColor: String,
    val textColor: String
)
