package edu.saracasas.fichajesapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ObtenerSemanaActual(): String {
    val currentDate = LocalDateTime.now()
    val startOfWeek = currentDate.with(java.time.DayOfWeek.MONDAY)
    val endOfWeek = currentDate.with(java.time.DayOfWeek.SUNDAY)

    val formatter = DateTimeFormatter.ofPattern("d MMM", Locale("es"))
    val formattedStart = startOfWeek.format(formatter)
    val formattedEnd = endOfWeek.format(formatter)

    return "Semana $formattedStart - $formattedEnd de ${currentDate.year}"
}


/** HorarioRow es un Composable que muestra una fila con la información de un día específico, incluyendo el día, la hora de entrada, la hora de salida y el total de horas trabajadas.
 * Se utiliza para mostrar cada uno de los fichajes registrados en la pantalla principal, con un
 */
@Composable
fun HorarioRow(dia: String, entrada: LocalDateTime, salida: LocalDateTime?, rojo: Color){

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val formattedIn = entrada.format(timeFormatter)
    val formattedOut = if (salida != null) salida.format(timeFormatter) else "-"

    val total = if (salida != null) {
        val durTemp = Duration.between(entrada, salida)
        String.format("%02d:%02d", durTemp.toHours(), durTemp.toMinutes() % 60)
    } else {
        "-"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .background(rojo, shape = RoundedCornerShape(6.dp))
                .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(dia, color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

//        Spacer(modifier = Modifier.width(16.dp))
        Text(text = formattedIn, color = Color.White, modifier = Modifier
            .weight(1f)
            .fillMaxWidth(), textAlign = TextAlign.Center)
        Text(text = formattedOut, color = Color.White, modifier = Modifier
            .weight(1f)
            .fillMaxWidth(), textAlign = TextAlign.Center)
        Text(text = total, color = Color.White, modifier = Modifier
            .weight(1f)
            .fillMaxWidth(), textAlign = TextAlign.Center)
    }
}