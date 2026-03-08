package edu.saracasas.fichajesapp.data.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class Empleado(
    @SerializedName("id") val id: Long = 0L,
    @SerializedName("nombre")  val nombre: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("password") val password: String,
    @SerializedName("rol") val rol: String,
    val createAt: LocalDateTime,
//    val fichajes: List<Fichaje>,
//    val horarios: List<Horario>
//    val accountNonExpired: Boolean,
//    val accountNonLocked: Boolean,
//    val credentialsNonExpired: Boolean,
//    val enabled: Boolean,
//    val authorities: HashMap<String, String>,
//    val username: String
)
