package edu.saracasas.fichajesapp.data.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class CalendarioLaboral(
    @SerializedName("id") val id: Long = 0L,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("anyo") val anyo: Int,
    @SerializedName("festivos") val festivos: List<Festivo>,
//    @SerializedName("empleados") val empleados: List<Empleado>,
    @SerializedName("createdAt") val createdAt: LocalDateTime
)
