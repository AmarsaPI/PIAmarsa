package edu.saracasas.fichajesapp.data

import edu.saracasas.fichajesapp.data.models.Empleado
import edu.saracasas.fichajesapp.data.models.Fichaje
import edu.saracasas.fichajesapp.data.remote.RemoteDatasource

class Repository(private val remoteDatasource: RemoteDatasource) {

    suspend fun getFichajes(): List<Fichaje>? {
        try {
            return remoteDatasource.getFichajes().body()
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    suspend fun getEmpleados(): List<Empleado>? {
        try {
            return remoteDatasource.getEmpeleados().body()
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    suspend fun loginEmpleado(email: String, password: String): Empleado? {
        try {
            return remoteDatasource.loginEmpleado(email, password).body()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}