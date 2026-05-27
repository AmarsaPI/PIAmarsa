package edu.saracasas.fichajesapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import edu.saracasas.fichajesapp.FichajesApp
import edu.saracasas.fichajesapp.viewmodels.MainViewModel
import kotlinx.coroutines.runBlocking

/** LoginScreen es un Composable que muestra la pantalla de login con campos para email y contraseña.
 * Permite al usuario ingresar sus credenciales y llamar a la función de login del ViewModel.
 * Si el login es exitoso, el estado del empleado logueado se actualizará y se mostrará la pantalla principal.
 * Si el login falla, se muestra un mensaje de error.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(vm: MainViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var intentoLogin by remember { mutableStateOf(false) }
    val empleadoLogueado by vm.empleadoLogueado.collectAsState()

    // Si no hay un empleado logueado, mostramos la pantalla de login
    if (empleadoLogueado == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Login",
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF5C6BC0),
                        titleContentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(0.8f)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(0.8f),
                    visualTransformation = PasswordVisualTransformation()
                )
                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            intentoLogin = true
                            // Se llama a la función para loguearse del ViewModel
                            runBlocking { vm.loginEmpleado(email, password) }
                        }
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(0.8f)
                ) {
                    Text("Iniciar Sesión")
                }
                if (intentoLogin) {
                    Text(
                        text = "Credenciales incorrectas",
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    } else {
        // Si el empleado ya está logueado, mostramos la pantalla principal
        FichajesApp(vm)
        return
    }
}