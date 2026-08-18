package ApunteIA.FrontEnd.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NotesScreen() {

    val notes = listOf(
        "Introducción a Kotlin",
        "Jetpack Compose",
        "Bases de Datos",
        "Programación Móvil"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Mis apuntes",
            style = MaterialTheme.typography.headlineMedium
        )

        LazyColumn {

            items(notes) { note ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {

                    Text(
                        text = note,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        }
    }
}