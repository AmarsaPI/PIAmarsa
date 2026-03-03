package edu.saracasas.fichajesapp

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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.saracasas.fichajesapp.ui.theme.FichajesAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FichajesApp()
        }
    }
}

@Composable
fun FichajesApp(){
    var selectedScreen by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(selectedIndex = selectedScreen, onItemSelected = {selectedScreen = it})
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            when (selectedScreen) {
                0 -> MainScreen()
                1 -> FicharScreen()
                /*2 -> PlaceHolderScreen("Calendario")
                3 -> PlaceHolderScreen("Enviar")*/
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(){
    val azulPrincipal = Color(0xFF5C6BC0)
    val azulCard = Color(0xFF7986CB)
    val rojoDia = Color(0xFFD32F2F)

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {Text("Inicio")},
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

        Column(modifier = Modifier.fillMaxSize().padding(16.dp),
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
                        text = "Usuario: Alberto González",
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = "11-17 Septiembre 2026",
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
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text= "Día",
                            color = Color.White,
                            modifier = Modifier.width(60.dp)
                        )
                        Text(
                            text= "Entrada",
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text= "Salida",
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text= "Total",
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Divider(color = Color.White.copy(alpha = 0.5f))

                    Spacer(modifier = Modifier.height(8.dp))

                    HorarioRow("11", "08:01", "16:03", "8:02", rojoDia)
                    HorarioRow("12", "07:58", "15:59", "8:01", rojoDia)
                    HorarioRow("13", "07:55", "-", "-", rojoDia)
                    HorarioRow("14", "-", "-", "-", rojoDia)
                    HorarioRow("15", "-", "-", "-", rojoDia)
                }
            }





            Spacer(modifier = Modifier.height(16.dp))

            //Card vacaciones
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

@Composable
fun HorarioRow(dia: String, entrada: String, salida: String, total: String, rojo: Color){
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.width(60.dp).background(rojo, shape = RoundedCornerShape(6.dp))
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(dia, color = Color.White)
        }

        Spacer(modifier = Modifier.width(16.dp))
        Text(text = entrada, color = Color.White, modifier = Modifier.weight(1f))
        Text(text = salida, color = Color.White, modifier = Modifier.weight(1f))
        Text(text = total, color = Color.White, modifier = Modifier.weight(1f))

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FicharScreen(){
    val azulPrincipal = Color(0xFF5C6BC0)
    val azulCard = Color(0xFF7986CB)

    Column{
        TopAppBar(
            title = {Text("Fichar")},
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
            modifier = Modifier.fillMaxSize().padding(16.dp)
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
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "No has fichado hoy",
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        )
                    ) {
                        Text("Fichar Entrada", color = azulPrincipal)
                    }
                }
            }
        }
    }
}

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


