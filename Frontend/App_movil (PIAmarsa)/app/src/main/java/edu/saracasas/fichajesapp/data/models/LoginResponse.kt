package edu.saracasas.fichajesapp.data.models

/**
 * LoginResponse es una clase de datos que representa la respuesta del servidor al intentar iniciar sesión.
 * Contiene un token de autenticación y un objeto Empleado con la información del empleado que ha iniciado sesión.
 */
data class LoginResponse(
    val token: String,
    val empleado: Empleado
)
