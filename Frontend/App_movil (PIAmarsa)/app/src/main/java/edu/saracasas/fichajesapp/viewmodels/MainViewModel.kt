package edu.saracasas.fichajesapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.saracasas.fichajesapp.MainActivity.Companion.tokenManager
import edu.saracasas.fichajesapp.data.Repository
import edu.saracasas.fichajesapp.data.models.Empleado
import edu.saracasas.fichajesapp.data.models.Fichaje
import edu.saracasas.fichajesapp.data.remote.FichajeApiService
import edu.saracasas.fichajesapp.data.remote.RemoteDatasource
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.time.LocalDateTime

/**
 * MainViewModel es una clase que extiende AndroidViewModel y se encarga de gestionar los datos relacionados con los empleados y fichajes.
 * Utiliza un Repository para acceder a los datos remotos y expone StateFlows para que la UI pueda observar los cambios en los datos.
 * También maneja el proceso de login y la verificación del token de autenticación.
 */
class MainViewModel(app: Application): AndroidViewModel(app) {
    private val repository: Repository
    private val remoteDatasource: RemoteDatasource

    // StateFlows para almacenar la lista de empleados, el empleado logueado y la lista de fichajes
    private val _allEmpleados = MutableStateFlow<List<Empleado>>(emptyList())
    val allEmpleados: StateFlow<List<Empleado>> = _allEmpleados

    // StateFlow para almacenar el empleado que ha iniciado sesión, inicialmente es null
    private val _empleadoLogueado = MutableStateFlow<Empleado?>(null)
    val empleadoLogueado: StateFlow<Empleado?> = _empleadoLogueado

    // StateFlow para almacenar la lista de fichajes, inicialmente vacía
    private val _allFichajes = MutableStateFlow<List<Fichaje>>(emptyList())
    val allFichajes: StateFlow<List<Fichaje>> = _allFichajes

    // StateFlow para almacenar la lista de fichajes de la semana actual, inicialmente vacía
    private val _fichajesSemanaActual = MutableStateFlow<List<Fichaje>>(emptyList())
    val fichajesSemanaActual: StateFlow<List<Fichaje>> = _fichajesSemanaActual

    // StateFlow para almacenar el fichaje actual, inicialmente null
    private val _fichajeActual = MutableStateFlow<Fichaje?>(null)
    val fichajeActual: StateFlow<Fichaje?> = _fichajeActual

    /** En el bloque init se inicializan el RemoteDatasource y el Repository, se verifica el token de autenticación y se carga la lista de empleados.
     * Se ejecuta en una corrutina dentro del viewModelScope para no bloquear el hilo principal.
     */
    init {
        remoteDatasource = RemoteDatasource()
        repository = Repository(remoteDatasource)
        viewModelScope.launch {
            comprobarToken()
            if (_empleadoLogueado.value != null) {
                getFichajesByEmpleadoIdSemanaActual()
                _fichajeActual.value =
                    if (_fichajesSemanaActual.value.lastOrNull()?.fechaSalida != null)
                        null
                    else
                        _fichajesSemanaActual.value.lastOrNull()
            }
        }
    }

    /** Función para obtener los fichajes de un empleado por su ID, actualiza el StateFlow de fichajes con los datos obtenidos del Repository.
     * Se ejecuta en una corrutina dentro del viewModelScope para no bloquear el hilo principal.
     */
    fun getFichajesByEmpleadoId() {
        viewModelScope.launch {
            _allFichajes.update {
                repository.getFichajesByEmpleadoId(_empleadoLogueado.value!!.id) ?: emptyList()
            }
        }
    }

    /** Función para obtener los fichajes de un empleado para la semana actual, actualiza el StateFlow de fichajes de la semana actual con los datos obtenidos del Repository.
     * Se ejecuta en una corrutina dentro del viewModelScope para no bloquear el hilo principal.
     */
    suspend fun getFichajesByEmpleadoIdSemanaActual() {
        _fichajesSemanaActual.update {
            repository.getFichajesByEmpleadoIdSemanaActual(_empleadoLogueado.value!!.id)
                ?: emptyList() }
    }

    /** Función para realizar el login de un empleado con su email y contraseña, actualiza el StateFlow del empleado logueado y guarda el token de autenticación.
     * Si el login es exitoso, se obtiene el token y se guarda utilizando el TokenManager, luego se llama a la función para obtener los fichajes del empleado.
     * Si el login falla, se establece el empleado logueado como null.
     * Se ejecuta en una corrutina dentro del viewModelScope para no bloquear el hilo principal.
     */
    fun loginEmpleado(email: String, password: String) {
        viewModelScope.launch {
            comprobarToken()

            val loginResult = async { repository.loginEmpleado(email, password) }.await()

            if (loginResult?.empleado != null) {
                _empleadoLogueado.value = loginResult.empleado

                tokenManager.saveAuthToken(loginResult.token)

                getFichajesByEmpleadoIdSemanaActual()
            } else {
                _empleadoLogueado.value = null
            }
        }
    }

    /** Función para fichar la entrada de un empleado, crea un nuevo objeto Fichaje con la información del empleado logueado y las fechas de entrada y salida, luego llama a la función del Repository para realizar el fichaje.
     * Después de fichar la entrada, se actualiza la lista de fichajes de la semana actual.
     * Se ejecuta en una corrutina dentro del viewModelScope para no bloquear el hilo principal.
     */
    fun ficharEntrada() {
        viewModelScope.launch {
            val fichaje = Fichaje(
                empleado = _empleadoLogueado.value!!,
                id = null
            )
            val fichajeDone = async { repository.ficharEntrada(fichaje) }.await()
            getFichajesByEmpleadoIdSemanaActual()
            _fichajeActual.value = _fichajesSemanaActual.value.last()
        }
    }

    fun ficharSalida() {
        viewModelScope.launch {
            val fichaje = Fichaje(
                empleado = _empleadoLogueado.value!!,
                id = _fichajeActual.value?.id
            )

            if (fichaje != null) {
                val fichajeDone = async { repository.ficharSalida(fichaje, fichaje.id!!) }.await()
                getFichajesByEmpleadoIdSemanaActual()
                _fichajeActual.value = null
            }
        }
    }

    /** Función para comprobar el token de autenticación, decodifica el token y verifica su validez. Si el token ha expirado, se borra el token y se restablece el estado del empleado logueado y la lista de fichajes.
     * Si el token es válido, se obtiene el ID del empleado del token, se obtiene la información del empleado desde el Repository y se actualiza el StateFlow del empleado logueado.
     * Luego se llama a la función para obtener los fichajes del empleado.
     * Se ejecuta en una corrutina dentro del viewModelScope para no bloquear el hilo principal.
     */
    suspend fun comprobarToken() {
        val decodedToken = FichajeApiService.decodedToken()

        if (decodedToken != null) {
            val tokenExp = JSONObject(decodedToken).getLong("exp") * 1000
            val systemTime = System.currentTimeMillis()
            if (tokenExp < systemTime) {
                tokenManager.clearToken()
                _empleadoLogueado.value = null
                _allFichajes.value = emptyList()
                return
            }

            val empleadoId = JSONObject(decodedToken).getLong("id")
            val empleado = repository.getEmpleadoById(empleadoId)
            _empleadoLogueado.value = empleado

        }
    }
}