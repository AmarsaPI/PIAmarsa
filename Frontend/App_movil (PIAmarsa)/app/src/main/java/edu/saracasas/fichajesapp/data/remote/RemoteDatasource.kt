package edu.saracasas.fichajesapp.data.remote

import edu.saracasas.fichajesapp.data.models.Fichaje

/**
 * RemoteDatasource es una clase que actúa como una capa de abstracción sobre el FichajeApiService.
 * Proporciona funciones para obtener empleados, fichajes y realizar el login, delegando las llamadas a la API.
 */
class RemoteDatasource {
    private val apiService = FichajeApiService.apiService
    suspend fun getEmpeleados() = apiService.getEmpleados()
    suspend fun getEmpleadoById(empleado_id: Long) = apiService.getEmpleadoById(empleado_id)
    suspend fun getFichajesByEmpleadoId(empleado_id: Long) = apiService.getFichajesByEmpleadoId(empleado_id)
    suspend fun getFichajesByEmpleadoIdSemanaActual(empleado_id: Long) = apiService.getFichajesByEmpleadoIdSemanActual(empleado_id)
    suspend fun loginEmpleado(email: String, password: String) = apiService.loginEmpleado(email, password)
    suspend fun ficharEntrada(fichaje: Fichaje) = apiService.ficharEntrada(fichaje)
    suspend fun ficharSalida(fichaje: Fichaje, id: Long) = apiService.ficharSalida(fichaje, id)
}