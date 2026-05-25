package edu.saracasas.fichajesapp.data

import edu.saracasas.fichajesapp.data.models.Empleado
import edu.saracasas.fichajesapp.data.models.Fichaje
import edu.saracasas.fichajesapp.data.models.FichajeResponse
import edu.saracasas.fichajesapp.data.models.LoginResponse
import edu.saracasas.fichajesapp.data.remote.RemoteDatasource

/**
 * Repository es una clase que actúa como intermediario entre el ViewModel y la fuente de datos remota.
 * Proporciona funciones para obtener fichajes, empleados y realizar el login, manejando las excepciones de forma adecuada.
 */
class Repository(private val remoteDatasource: RemoteDatasource) {

    // Función para obtener los fichajes de un empleado por su ID
    suspend fun getFichajesByEmpleadoId(empleado_id: Long): List<Fichaje>? {
        try {
            return remoteDatasource.getFichajesByEmpleadoId(empleado_id).body()
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    // Función para obtener los fichajes de un empleado por su ID para la semana actual
    suspend fun getFichajesByEmpleadoIdSemanaActual(empleado_id: Long): List<Fichaje>? {
        try {
            return remoteDatasource.getFichajesByEmpleadoIdSemanaActual(empleado_id).body()
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    // Función para obtener la lista de empleados
    suspend fun getEmpleados(): List<Empleado>? {
        try {
            return remoteDatasource.getEmpeleados().body()
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    // Función para obtener un empleado por su ID
    suspend fun getEmpleadoById(empleado_id: Long): Empleado? {
        try {
            return remoteDatasource.getEmpleadoById(empleado_id).body()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    // Función para realizar el login de un empleado con su email y contraseña
    suspend fun loginEmpleado(email: String, password: String): LoginResponse? {
        try {
            return remoteDatasource.loginEmpleado(email, password).body()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun ficharEntrada(fichaje: Fichaje): FichajeResponse? {
            try {
                return remoteDatasource.ficharEntrada(fichaje).body()
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
    }

    suspend fun ficharSalida(fichaje: Fichaje, id: Long): FichajeResponse? {
        try {
            return remoteDatasource.ficharSalida(fichaje, id).body()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}