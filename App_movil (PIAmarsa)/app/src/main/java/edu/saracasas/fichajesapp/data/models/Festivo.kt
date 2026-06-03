package edu.saracasas.fichajesapp.data.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalDateTime

data class Festivo (
    @SerializedName("id") val id: Long = 0L,
    @SerializedName("fecha") val fecha: LocalDate,
    @SerializedName("descripcion") val descripcion: String,
//    @SerializedName("calendario") val calendario: CalendarioLaboral
)