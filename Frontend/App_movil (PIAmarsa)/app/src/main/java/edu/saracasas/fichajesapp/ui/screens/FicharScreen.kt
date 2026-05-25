package edu.saracasas.fichajesapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.saracasas.fichajesapp.viewmodels.MainViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** FicharScreen es un Composable que muestra la pantalla de fichaje, con información sobre el estado actual del fichaje y un botón para fichar la entrada.
 * Se utiliza para permitir al usuario registrar su fichaje de entrada, mostrando un mensaje si no ha fichado aún y un botón para realizar el fichaje.
 * La pantalla incluye una barra superior con el título "Fichar" y una tarjeta que muestra el estado actual del fichaje.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FicharScreen(vm: MainViewModel) {
    val azulPrincipal = Color(0xFF5C6BC0)
    val azulCard = Color(0xFF7986CB)
    val verdeFichaje = Color(0xFF388E3C)
    val rojoFichaje = Color(0xFFD32F2F)
    val formatoHora = DateTimeFormatter.ofPattern("HH:mm 'del' d MMM", Locale("es"))

    val empleadoLogueado by vm.empleadoLogueado.collectAsState()
    val fichajeActual by vm.fichajeActual.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Fichar") },
            navigationIcon = {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Person, contentDescription = "Perfil")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = azulPrincipal,
                titleContentColor = Color.White
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = azulCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Estado actual",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text =
                            if (fichajeActual == null) {
                                "No has fichado hoy"
                            } else {
                                "Has fichado la entrada a las ${fichajeActual!!.fechaEntrada?.format(formatoHora)}"
                            },
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (fichajeActual == null)
                                vm.ficharEntrada()
                            else
                                vm.ficharSalida()
                                  },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (fichajeActual == null) verdeFichaje else rojoFichaje
                        )
                    ) {
                        Text(
                            "Fichar " + if (fichajeActual == null) "Entrada" else "Salida",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
