package edu.saracasas.fichajesapp.data.remote

import edu.saracasas.fichajesapp.data.models.Empleado
import edu.saracasas.fichajesapp.data.models.Fichaje

class RemoteDatasource {
    private val apiService = FichajeApiService.apiService

    suspend fun getEmpeleados() = apiService.getEmpleados()
    suspend fun getFichajes() = apiService.getFichajes()

    suspend fun loginEmpleado(email: String, password: String) = apiService.loginEmpleado(email, password)
}