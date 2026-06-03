package edu.saracasas.fichajesapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.remote.creation.compose.shaders.linearGradient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.computeHorizontalBounds
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.saracasas.fichajesapp.FichajesApp
import edu.saracasas.fichajesapp.viewmodels.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.lang.Compiler.enable

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
//        Scaffold(
//            topBar = {
//                TopAppBar(
//                    title = {
//                        Text(
//                            "HoraFlow",
//                            modifier = Modifier
//                                .padding(8.dp)
//                                .fillMaxWidth(),
//                            textAlign = TextAlign.Center
//                        )
//                    },
//                    colors = TopAppBarDefaults.topAppBarColors(
//                        containerColor = Color(0xFF5C6BC0),
//                        titleContentColor = Color.White
//                    ),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                )
//            }
//        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF5C6BC0))
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Bienvenid@ a HoraFlow",
                    style = typography.headlineMedium,
                    color = Color.White
                )

                Text(
                    "Por favor, ingresa tus credenciales para continuar",
                    style = typography.bodyMedium,
                    color = Color.LightGray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        intentoLogin = false},
                    label = { Text("Email") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(0.8f),
                    leadingIcon = {
                        Icon(
                            Default.Person,
                            contentDescription = "Email Icon"
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.LightGray,
                        focusedLeadingIconColor = Color.White,
                        unfocusedLeadingIconColor = Color.LightGray,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.LightGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )

                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        intentoLogin = false },
                    label = { Text("Contraseña") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(0.8f),
                    leadingIcon = {
                        Icon(
                            Default.Edit,
                            contentDescription = "Email Icon"
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.LightGray,
                        focusedLeadingIconColor = Color.White,
                        unfocusedLeadingIconColor = Color.LightGray,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.LightGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
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
                        .fillMaxWidth(0.8f),
                    enabled = email.isNotBlank() && password.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF5C6BC0)
                    )
                ) {
                    Text("Iniciar Sesión")
                }
                if (intentoLogin && empleadoLogueado == null) {
                    Text(
                        text = "Credenciales incorrectas",
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

    } else {
        // Si el empleado ya está logueado, mostramos la pantalla principal
        FichajesApp(vm)
        return
    }
}