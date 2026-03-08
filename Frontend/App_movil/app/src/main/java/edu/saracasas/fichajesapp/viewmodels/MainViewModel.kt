package edu.saracasas.fichajesapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.saracasas.fichajesapp.data.Repository
import edu.saracasas.fichajesapp.data.models.Empleado
import edu.saracasas.fichajesapp.data.models.Fichaje
import edu.saracasas.fichajesapp.data.remote.RemoteDatasource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(app: Application): AndroidViewModel(app) {
    private val repository: Repository
    private val remoteDatasource: RemoteDatasource

    private val _allEmpleados = MutableStateFlow<List<Empleado>>(emptyList())
    val allEmpleados: StateFlow<List<Empleado>> = _allEmpleados

    private val _empleadoLogueado = MutableStateFlow<Empleado?>(null)
    val empleadoLogueado: StateFlow<Empleado?> = _empleadoLogueado

    private val _allFichajes = MutableStateFlow<List<Fichaje>>(emptyList())
    val allFichajes: StateFlow<List<Fichaje>> = _allFichajes

    init {
        remoteDatasource = RemoteDatasource()
        repository = Repository(remoteDatasource)

        viewModelScope.launch {
            _allEmpleados.update { repository.getEmpleados() ?: emptyList() }
        }
    }

    suspend fun getEmpleados() = repository.getEmpleados()
    suspend fun getFichajes() = repository.getFichajes()

    fun loginEmpleado(email: String, password: String) {
        viewModelScope.launch {
            _empleadoLogueado.value = repository.loginEmpleado(email, password)
        }
    }
}