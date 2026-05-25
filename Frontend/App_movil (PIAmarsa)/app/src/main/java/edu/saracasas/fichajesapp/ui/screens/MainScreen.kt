package edu.saracasas.fichajesapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import java.time.format.DateTimeFormatter
import java.util.Locale

/** MainScreen es un Composable que muestra la pantalla principal de la aplicación, con información sobre el horario semanal del empleado logueado y sus fichajes.
 * Utiliza el estado del empleado logueado y la lista de fichajes obtenidos del ViewModel para mostrar la información relevante.
 * Incluye una barra superior con el título "Inicio" y una tarjeta que muestra el horario semanal, así como otra tarjeta que muestra los fichajes registrados.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(vm: MainViewModel) {
    val azulPrincipal = Color(0xFF5C6BC0)
    val azulCard = Color(0xFF7986CB)
    val rojoDia = Color(0xFFD32F2F)

    val empleadoLogueado by vm.empleadoLogueado.collectAsState()
    val fichajesSemanaActual by vm.fichajesSemanaActual.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Inicio") },
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
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White,
                actionIconContentColor = Color.White
            )
        )

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {

            //Card fichajes
            Card(
                colors = CardDefaults.cardColors(containerColor = azulCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Mi horario semanal",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = empleadoLogueado?.nombre ?: "Empleado",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = ObtenerSemanaActual(),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = azulCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text= "Día",
                            color = Color.White,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text= "Entrada",
                            color = Color.White,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text= "Salida",
                            color = Color.White,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text= "Total",
                            color = Color.White,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    HorizontalDivider(
                        Modifier,
                        DividerDefaults.Thickness,
                        color = Color.White.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (fichajesSemanaActual.isEmpty()) {
                        Text(
                            text = "No hay fichajes registrados",
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        // Se muestran los fichajes del empleado logueado
                        val dateTimeFormatter = DateTimeFormatter.ofPattern("eee, d MMM",
                            Locale("es")
                        )
                        fichajesSemanaActual.forEach { fichaje ->
                            HorarioRow(
                                dia = fichaje.fechaEntrada!!.format(dateTimeFormatter),
                                entrada = fichaje.fechaEntrada!!,
                                salida = fichaje.fechaSalida,
                                rojo = rojoDia
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card vacaciones
            Card(
                colors = CardDefaults.cardColors(containerColor = azulCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)

                ) {
                    Text(
                        text = "Vacaciones",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = "06-19 Abril 2026",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}