package edu.saracasas.fichajesapp.data.remote

import edu.saracasas.fichajesapp.data.models.Empleado
import edu.saracasas.fichajesapp.data.models.Fichaje
import edu.saracasas.fichajesapp.data.models.FichajeResponse
import edu.saracasas.fichajesapp.data.models.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

/**
 * ApiService es una interfaz que define los endpoints de la API REST utilizando Retrofit.
 * Cada función corresponde a una llamada HTTP específica (GET, POST) y devuelve un Response con el tipo de dato esperado.
 */
interface ApiService {
    @GET("fichajes/empleado/{empleadoId}")
    suspend fun getFichajesByEmpleadoId(
        @retrofit2.http.Path("empleadoId") empleado_id: Long): Response<List<Fichaje>>

    @GET("fichajes/empleado/{empleadoId}/SemanaActual")
    suspend fun getFichajesByEmpleadoIdSemanActual(
        @retrofit2.http.Path("empleadoId") empleado_id: Long): Response<List<Fichaje>>

    @GET("empleados")
    suspend fun getEmpleados(): Response<List<Empleado>>

    @GET("empleados/{empleadoId}")
    suspend fun getEmpleadoById(@retrofit2.http.Path("empleadoId") empleado_id: Long): Response<Empleado>

    @POST("login/{email}/{password}")
    suspend fun loginEmpleado(
        @retrofit2.http.Path("email") email: String,
        @retrofit2.http.Path("password") password: String): Response<LoginResponse>

    @POST("fichajes")
    suspend fun ficharEntrada(@Body fichaje: Fichaje): Response<FichajeResponse>

    @PUT("fichajes/{id}")
    suspend fun ficharSalida(@Body fichaje: Fichaje,
                             @retrofit2.http.Path("id") id: Long): Response<FichajeResponse>
}