package edu.saracasas.fichajesapp.data.remote

import edu.saracasas.fichajesapp.data.models.Empleado
import edu.saracasas.fichajesapp.data.models.Fichaje
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("fichajes")
    suspend fun getFichajes(): Response<List<Fichaje>>

    @GET("empleados")
    suspend fun getEmpleados(): Response<List<Empleado>>

    @GET("empleados/login/{email}/{password}")
    suspend fun loginEmpleado(
        @retrofit2.http.Path("email") email: String,
        @retrofit2.http.Path("password") password: String): Response<Empleado>
}