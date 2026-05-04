package edu.saracasas.fichajesapp.data.remote

import edu.saracasas.fichajesapp.data.models.Empleado
import edu.saracasas.fichajesapp.data.models.Fichaje
import okhttp3.Response

class RemoteDatasource {
    private val apiService = FichajeApiService.apiService

    suspend fun getEmpeleados() = apiService.getEmpleados()
    suspend fun getFichajesByEmpleadoId(empleado_id: Long) = apiService.getFichajesByEmpleadoId(empleado_id)

    suspend fun loginEmpleado(email: String, password: String) = apiService.loginEmpleado(email, password)
}