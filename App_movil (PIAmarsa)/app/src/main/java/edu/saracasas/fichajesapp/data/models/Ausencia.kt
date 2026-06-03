package edu.saracasas.fichajesapp.data.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class Ausencia(
    @SerializedName("id") val id: Long = 0L,
    @SerializedName("empleado") val empleado: Empleado,
    @SerializedName("fechaInicio") val fechaInicio: LocalDate,
    @SerializedName("fechaFin") val fechaFin: LocalDate,
    @SerializedName("tipo") val tipo: TipoAusencia,
    @SerializedName("estado") val estado: EstadoAusencia
)