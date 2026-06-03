package edu.saracasas.fichajesapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.saracasas.fichajesapp.data.models.TipoAusencia
import edu.saracasas.fichajesapp.viewmodels.MainViewModel
import java.time.LocalDate
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
    val verdeFichaje = Color(0xFF388E3C)
    val rojoDia = Color(0xFFD32F2F)
    val verdeVacas = Color(0xFF00FC05)
    val rojoBaja = Color(0xFFC90707)
    val azulPR = Color(0xFF072FC9)

    val formatter = DateTimeFormatter.ofPattern("d MMM", Locale("es"))

    val empleadoLogueado by vm.empleadoLogueado.collectAsState()
    val fichajesSemanaActual by vm.fichajesSemanaActual.collectAsState()
    val vacaciones by vm.ausenciasEmpleado.collectAsState()
    val startWeek by vm.startWeek.collectAsState()
    val endWeek by vm.endWeek.collectAsState()

    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
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
                        text = "Bienvenid@!",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = empleadoLogueado?.nombre ?: "Desconocido",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Calendario laboral aplicado",
                        color = Color.Black,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = empleadoLogueado?.calendario?.nombre ?: "No Asignado",
                        color = Color.LightGray,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = azulCard),
                modifier = Modifier.fillMaxWidth()
                    .background(azulCard, RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                    .border(0.5.dp, Color.Gray, RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
            ) {

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Fichajes Semanales",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Row(modifier = Modifier
                        .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {

                        IconButton(
                            onClick = {
                                vm.semanaActualView(false)
                            },
                            colors = IconButtonDefaults.iconButtonColors(Color.White)
                        ) {
                            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Semana menos")
                        }

                        val formattedStart = startWeek.format(formatter)
                        val formattedEnd = endWeek.format(formatter)

                        Text(
                            text = "$formattedStart - $formattedEnd de ${endWeek.year}",
                            fontSize = 16.sp,
                            color = Color.LightGray
                        )

                        IconButton(
                            onClick = {
                                vm.semanaActualView()
                            },
                            enabled = LocalDate.now() > endWeek,
                            colors = IconButtonDefaults.iconButtonColors(Color.White)
                        ) {
                            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Semana más")
                        }

                    }
                }
            }


            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
                    .border(0.5.dp, Color.LightGray, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Día",
                        color = Color.Black,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Entrada",
                        color = Color.Black,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Salida",
                        color = Color.Black,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Total",
                        color = Color.Black,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                HorizontalDivider(
                    Modifier,
                    DividerDefaults.Thickness,
                    color = Color.Black.copy(alpha = 0.1f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (fichajesSemanaActual.isEmpty()) {
                    Text(
                        text = "No hay fichajes registrados",
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else {
                    // Se muestran los fichajes del empleado logueado
                    val dateTimeFormatter = DateTimeFormatter.ofPattern(
                        "eee, d MMM",
                        Locale("es")
                    )
                    fichajesSemanaActual.forEach { fichaje ->
                        HorarioRow(
                            dia = fichaje.fechaEntrada!!.format(dateTimeFormatter),
                            entrada = fichaje.fechaEntrada!!,
                            salida = fichaje.fechaSalida,
                            rojo = if (fichaje.fechaSalida == null) verdeFichaje else rojoDia
                        )
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
                        text = "Ausencias",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        vacaciones?.forEach {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${it.tipo}: ",
                                    color =
                                        when (it.tipo) {
                                            TipoAusencia.VACACIONES -> verdeVacas
                                            TipoAusencia.BAJA_MEDICA ->  rojoBaja
                                            TipoAusencia.PERMISO_RETRIBUIDO -> azulPR
                                        },
                                    fontSize = 16.sp
                                )

                                Text(
                                    text = "${it.fechaInicio.format(formatter)} - ${
                                        it.fechaFin.format(
                                            formatter
                                        )
                                    }",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.padding(4.dp))
                        }
                    }
                }
            }
        }
    }
}