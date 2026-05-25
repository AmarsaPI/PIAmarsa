package edu.saracasas.fichajesapp

import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toString
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.saracasas.fichajesapp.data.remote.FichajeApiService
import edu.saracasas.fichajesapp.data.remote.TokenManager
import edu.saracasas.fichajesapp.ui.screens.FicharScreen
import edu.saracasas.fichajesapp.ui.screens.LoginScreen
import edu.saracasas.fichajesapp.ui.screens.MainScreen
import edu.saracasas.fichajesapp.viewmodels.MainViewModel
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.Locale

class MainActivity : ComponentActivity() {
    companion object {
            lateinit var tokenManager: TokenManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Inicializamos el TokenManager con el contexto de la aplicación
        FichajeApiService.init(applicationContext)
        setContent {
            /** Si el token es válido, el ViewModel actualizará el estado del empleado logueado y se mostrará la pantalla principal.
             * Si el token no es válido o no existe, el estado del empleado logueado será null y se mostrará la pantalla de login.
             */
            val vm : MainViewModel = viewModel()
            // Llamamos a la función principal de la app, pasando el ViewModel
            FichajesApp(vm)
        }
    }
}

/** FichajesApp es el Composable principal de la aplicación, que muestra la pantalla principal con una barra de navegación inferior.
 * Permite al usuario navegar entre diferentes pantallas (Inicio, Fichar, Calendario, Enviar) y muestra la pantalla de login si no hay un empleado logueado.
 * El estado del empleado logueado se obtiene del ViewModel y se actualiza automáticamente cuando el usuario inicia sesión o cierra sesión.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FichajesApp(vm: MainViewModel) {
    var selectedScreen by remember { mutableStateOf(0) }

    // Obtenemos el estado del empleado logueado desde el ViewModel
    val empleadoLogueado by vm.empleadoLogueado.collectAsState()

    // Si no hay un empleado logueado, mostramos la pantalla de login
    if (empleadoLogueado == null) {
        LoginScreen(vm)
        return
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(selectedIndex = selectedScreen, onItemSelected = {selectedScreen = it})
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedScreen) {
                0 -> MainScreen(vm)
                1 -> FicharScreen(vm)
                /*2 -> PlaceHolderScreen("Calendario")
                3 -> PlaceHolderScreen("Enviar")*/
            }
        }
    }
}



/** BottomNavigationBar es un Composable que muestra una barra de navegación inferior con iconos para las diferentes pantallas de la aplicación (Inicio, Fichar, Calendario, Enviar).
 * Permite al usuario seleccionar una pantalla y actualiza el estado de la pantalla seleccionada en la función principal de la aplicación.
 * Cada icono representa una pantalla diferente y se resalta el icono correspondiente a la pantalla actualmente seleccionada.
 */
@Composable
fun BottomNavigationBar(selectedIndex: Int, onItemSelected: (Int) -> Unit){
    NavigationBar {
        NavigationBarItem(
            selected = selectedIndex == 0,
            onClick = {onItemSelected(0)},
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") }
        )

        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = {onItemSelected(1)},
            icon = { Icon(Icons.Default.AddCircle, contentDescription = "Fichar") }
        )

        NavigationBarItem(
            selected = selectedIndex == 2,
            onClick = {onItemSelected(2)},
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Calendario") }
        )

        NavigationBarItem(
            selected = selectedIndex == 3,
            onClick = {onItemSelected(3)},
            icon = { Icon(Icons.Default.Send, contentDescription = "Enviar") }
        )
    }
}