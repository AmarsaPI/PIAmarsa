package edu.saracasas.fichajesapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.saracasas.fichajesapp.viewmodels.MainViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import androidx.core.graphics.toColorInt
import edu.saracasas.fichajesapp.data.models.Horario
import edu.saracasas.fichajesapp.data.models.HorarioResponse
import edu.saracasas.fichajesapp.data.models.TipoAusencia
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorarioScreen(vm: MainViewModel) {
    val azulCard = Color(0xFF7986CB)

    val azulPrincipal = Color(0xFF5C6BC0)
    val rojoFestivo = Color(0x66FF0000)
    val verdeVacas = Color(0xFF00FC05)
    val verdeVacasLeyenda = Color(0xFF2D9B2F)
    val rojoBajaLeyenda = Color(0xFFC90707)
    val azulPRLeyenda = Color(0xFF072FC9)

    val formatter = DateTimeFormatter.ofPattern("d MMM", Locale("es"))

    val detalleHorario = remember { mutableStateOf<HorarioResponse?>(null) }

    // Estado para controlar el mes actual que se está visualizando
    val mesActual = vm.currentMonth.collectAsState()
    val listHorarios by vm.horariosEmpleado.collectAsState()
    val horarios = listHorarios?.associateBy { it.start }
    val festivos = vm.empleadoLogueado.collectAsState().value?.calendario?.festivos
    val vacaciones by vm.ausenciasEmpleado.collectAsState()

    // Obtener los días del mes y los huecos en blanco al principio (si el mes no empieza en Lunes)
    val diasDelMes = obtenerDiasDelMes(mesActual.value)
    val diasSemana = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")

    Column(modifier = Modifier.fillMaxSize()) {

        // Encabezado con el mes y botones de navegación
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {

            // 1. Cabecera: Flecha Izquierda - MES - Flecha Derecha
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(azulPrincipal, shape = RoundedCornerShape(10.dp)).padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        vm.mesActualView(false)
                    },
                    colors = IconButtonDefaults.iconButtonColors(Color.White)
                ) { Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Mes Anterior") }

                Text(
                    text = mesActual.value.month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
                        .replaceFirstChar { it.uppercase() } + " ${mesActual.value.year}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                IconButton(
                    onClick = {
                        vm.mesActualView()
                    },
                    colors = IconButtonDefaults.iconButtonColors(Color.White)
                ) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Mes Posterior")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Cabecera de los días de la semana (Lun, Mar, Mié...)
            Row(modifier = Modifier.fillMaxWidth()) {
                diasSemana.forEach { dia ->
                    Text(
                        text = dia,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (dia == "Sáb" || dia == "Dom")
                            Color.Red
                        else
                            Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Cuadrícula del Calendario (7 columnas fijas)
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(diasDelMes) { fecha ->
                    if (fecha != null) {
                        var isFestivo = false
                        if (festivos != null) {
                            for (festivo in festivos) {
                                if (festivo.fecha == fecha) {
                                    isFestivo = true
                                }
                            }
                            if (fecha.dayOfWeek == DayOfWeek.SATURDAY || fecha.dayOfWeek == DayOfWeek.SUNDAY) {
                                isFestivo = true
                            }
                        }
                        // Si el día pertenece al mes, buscamos si tiene horario
                        val horarioAsignado = horarios?.get(fecha.toString())

                        Column(
                            modifier = Modifier
                                .aspectRatio(0.85f) // Un pelín más alto que ancho para que quepa el texto
                                .padding(1.dp)
                                .border(
                                    0.5.dp,
                                    if (horarioAsignado == null) Color.LightGray else Color.Gray,
                                    shape = RoundedCornerShape(7.dp)
                                )
                                .background(
                                    if (isFestivo) rojoFestivo
                                    else Color(
                                        horarioAsignado?.backgroundColor?.toColorInt() ?: 0x00FFFFFF
                                    ),
                                    shape = RoundedCornerShape(7.dp)
                                )
                                .clickable(onClick = { detalleHorario.value = horarioAsignado }),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Número del día
                            Text(
                                text = fecha.dayOfMonth.toString(),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 4.dp),
                                fontWeight = if (fecha == LocalDate.now()) FontWeight.ExtraBold else FontWeight.Normal,
                                color = if (fecha == LocalDate.now()) Color.Cyan else Color.Black // Marca el día de hoy
                            )

                            // Muestra el horario asignado si existe
                            if (horarioAsignado != null) {
                                Text(
                                    text = horarioAsignado.title.split("/")[1],
                                    fontSize = 12.sp, // Letra pequeñita para que no desborde la celda
                                    color = Color.Black,
                                    maxLines = 2,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .padding(bottom = 4.dp)
                                        .fillMaxWidth()
                                )
                            } else {
                                Spacer(modifier = Modifier.height(1.dp))
                            }
                        }
                    } else {
                        // Celdas vacías al principio del mes (días del mes anterior)
                        Box(modifier = Modifier.aspectRatio(0.85f))
                    }
                }
            }

            if (detalleHorario.value != null) {
                alertDialogo(
                    detalleHorario.value?.start ?: "Fecha",
                    { detalleHorario.value = null },
                    detalleHorario.value?.title?.split("/")?.get(0) ?: "",
                    detalleHorario.value?.title?.split("/")?.get(1) ?: ""
                )
            }


            Spacer(modifier = Modifier.height(8.dp))

            // 4. Leyenda de colores
            Row(
                modifier = Modifier.fillMaxWidth().border(
                    0.5.dp, Color.LightGray, shape = RoundedCornerShape(10.dp)
                ).background(Color.White, shape = RoundedCornerShape(10.dp)).padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
            )
            {
                Text(
                    text = "V - Vacaciones",
                    fontSize = 14.sp,
                    color = verdeVacasLeyenda
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "P - Permisos",
                    fontSize = 14.sp,
                    color = azulPRLeyenda
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "B - Baja",
                    fontSize = 14.sp,
                    color = rojoBajaLeyenda
                )
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

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        vacaciones?.forEach {
                            if (it.tipo == TipoAusencia.VACACIONES) {

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${it.tipo}: ",
                                        color = verdeVacas,
                                        fontSize = 16.sp
                                    )

                                    Text(
                                        text = "${it.fechaInicio.format(formatter)} - ${
                                            it.fechaFin.format(
                                                formatter
                                            )
                                        } (${it.estado})",
                                        color = Color.White,
                                        fontSize = 16.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// Función auxiliar para calcular los días exactos de la cuadrícula
fun obtenerDiasDelMes(yearMonth: YearMonth): List<LocalDate?> {
    val lista = mutableListOf<LocalDate?>()
    val primerDiaDelMes = yearMonth.atDay(1)
    val totalDias = yearMonth.lengthOfMonth()

    // Obtener el día de la semana en que empieza (1 = Lunes, 7 = Domingo)
    val diaSemanaInicio = primerDiaDelMes.dayOfWeek.value

    // Rellenar con nulos los huecos de la primera semana si el mes no empieza en lunes
    for (i in 1 until diaSemanaInicio) {
        lista.add(null)
    }

    // Añadir todos los días reales del mes
    for (dia in 1..totalDias) {
        lista.add(yearMonth.atDay(dia))
    }

    return lista
}

@Composable
fun alertDialogo(
    fecha: String,
    onDismiss: () -> Unit,
    title: String,
    message: String
) {
    val fechaTitle = LocalDate.parse(fecha).format(DateTimeFormatter.ofPattern("dd MMMM yyyy")
        .withLocale(Locale("es", "ES")))
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = fechaTitle) },
        text = { Column() {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = message)
                }
        },
        confirmButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    "Ok",
                    color = Color(0xFF444A8C)
                )
            }
        },
        containerColor = Color(0xFF444A8C),
        titleContentColor = Color.White,
        textContentColor = Color.LightGray,
    )
}