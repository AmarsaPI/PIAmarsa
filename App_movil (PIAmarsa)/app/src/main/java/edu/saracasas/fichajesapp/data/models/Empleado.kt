package edu.saracasas.fichajesapp.data.models

import android.icu.util.Calendar
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

/**
 * Empleado es una clase de datos que representa a un empleado en el sistema de fichajes.
 * Contiene información como el ID, nombre, email, contraseña, rol y fecha de creación del empleado.
 */
data class Empleado(
    @SerializedName("id") val id: Long = 0L,
    @SerializedName("nombre")  val nombre: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("password") val password: String,
    @SerializedName("rol") val rol: String,
    @SerializedName("activo") val activo: Boolean,
    @SerializedName("fichajes") val fichajes: List<Fichaje>,
    @SerializedName("calendario") val calendario: CalendarioLaboral,
    @SerializedName("contratos") val contratos: List<Contrato>,
    @SerializedName("createdAt") val createdAt: LocalDateTime
)
