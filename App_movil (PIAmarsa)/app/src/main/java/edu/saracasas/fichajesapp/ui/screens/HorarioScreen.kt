package edu.saracasas.fichajesapp.ui.screens

import android.R
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.saracasas.fichajesapp.viewmodels.MainViewModel
import java.sql.Date
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import java.util.function.BinaryOperator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorarioScreen(vm: MainViewModel) {

    val azulPrincipal = Color(0xFF5C6BC0)
    // Estado para controlar el mes actual que se está visualizando
    var mesActual by remember { mutableStateOf(YearMonth.now()) }
    val listHorarios by vm.horariosEmpleado.collectAsState()
    val horarios = listHorarios?.associateBy { it.start }

    // Obtener los días del mes y los huecos en blanco al principio (si el mes no empieza en Lunes)
    val diasDelMes = remember(mesActual) { obtenerDiasDelMes(mesActual) }
    val diasSemana = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")

    Column(modifier = Modifier
        .fillMaxSize().padding(16.dp)) {

        // 1. Cabecera: Flecha Izquierda - MES - Flecha Derecha
        Row(
            modifier = Modifier.fillMaxWidth().background(azulPrincipal, shape = RoundedCornerShape(10.dp)).padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { mesActual = mesActual.minusMonths(1) },
                colors = IconButtonDefaults.iconButtonColors(Color.White)
                ) { Icon(Icons.Default.ArrowBack, contentDescription = "Mes Anterior") }

            Text(
                text = mesActual.month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
                    .replaceFirstChar { it.uppercase() } + " ${mesActual.year}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            IconButton(onClick = { mesActual = mesActual.plusMonths(1) },
                colors = IconButtonDefaults.iconButtonColors(Color.White)) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Mes Posterior") }
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
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(diasDelMes) { fecha ->
                if (fecha != null) {
                    // Si el día pertenece al mes, buscamos si tiene horario
                    val horarioAsignado = horarios?.get(fecha.toString())?.title

                    Column(
                        modifier = Modifier
                            .aspectRatio(0.85f) // Un pelín más alto que ancho para que quepa el texto
                            .padding(1.dp)
                            .border(0.5.dp, if (horarioAsignado == null) Color.LightGray else Color.Green, shape = RoundedCornerShape(7.dp))
                            .background(if (horarioAsignado != null) Color(0xFFE3F2FD) else Color.Transparent, shape = RoundedCornerShape(7.dp)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Número del día
                        Text(
                            text = fecha.dayOfMonth.toString(),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp),
                            fontWeight = if (fecha == LocalDate.now()) FontWeight.ExtraBold else FontWeight.Normal,
                            color = if (fecha == LocalDate.now()) Color.Red else Color.Black // Marca el día de hoy
                        )

                        // Muestra el horario asignado si existe
                        if (horarioAsignado != null) {
                            Text(
                                text = horarioAsignado,
                                fontSize = 12.sp, // Letra pequeñita para que no desborde la celda
                                color = Color(0xFF1976D2),
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