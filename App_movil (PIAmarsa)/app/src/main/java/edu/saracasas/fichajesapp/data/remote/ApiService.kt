package edu.saracasas.fichajesapp.data.remote

import edu.saracasas.fichajesapp.data.models.Empleado
import edu.saracasas.fichajesapp.data.models.Fichaje
import edu.saracasas.fichajesapp.data.models.FichajeResponse
import edu.saracasas.fichajesapp.data.models.HorarioResponse
import edu.saracasas.fichajesapp.data.models.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDateTime

/**
 * ApiService es una interfaz que define los endpoints de la API REST utilizando Retrofit.
 * Cada función corresponde a una llamada HTTP específica (GET, POST) y devuelve un Response con el tipo de dato esperado.
 */
interface ApiService {
    @GET("fichajes/empleado/{empleadoId}")
    suspend fun getFichajesByEmpleadoId(
        @Path("empleadoId") empleado_id: Long): Response<List<Fichaje>>

    @GET("fichajes/empleado/{empleadoId}/SemanaActual")
    suspend fun getFichajesByEmpleadoIdSemanActual(
        @Path("empleadoId") empleado_id: Long): Response<List<Fichaje>>

    @GET("empleados")
    suspend fun getEmpleados(): Response<List<Empleado>>

    @GET("empleados/{empleadoId}")
    suspend fun getEmpleadoById(@Path("empleadoId") empleado_id: Long): Response<Empleado>

    @POST("login/{email}/{password}")
    suspend fun loginEmpleado(
        @Path("email") email: String,
        @Path("password") password: String): Response<LoginResponse>

    @POST("fichajes")
    suspend fun ficharEntrada(@Body fichaje: Fichaje): Response<FichajeResponse>

    @PUT("fichajes/{id}")
    suspend fun ficharSalida(@Body fichaje: Fichaje,
                             @Path("id") id: Long): Response<FichajeResponse>

    @GET("horarios-reales/empleado/{empleadoId}")
    suspend fun getHorariosByEmpleadoId(@Path("empleadoId") empleadoId: Long,
                                @Query("start") start: String,
                                @Query("end") end: String): Response<List<HorarioResponse>?>
}