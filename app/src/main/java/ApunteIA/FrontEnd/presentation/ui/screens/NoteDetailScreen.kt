package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.domain.model.NoteStatus
import ApunteIA.FrontEnd.presentation.ui.components.StatusTag
import ApunteIA.FrontEnd.presentation.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteTitle: String,
    onBack: () -> Unit
) {
    // Datos de ejemplo extendidos para la nota
    val fullContent = "Los centros quirales surgen cuando un carbono se une a cuatro grupos diferentes. Los enantiómeros son imágenes especulares no superponibles, lo cual es fundamental para entender la reactividad biológica y la síntesis de fármacos. En química orgánica, la quiralidad es una propiedad geométrica de algunas moléculas e iones. Se dice que un objeto es quiral si no es superponible con su imagen especular."
    
    val keyPoints = listOf(
        "Carbono asimétrico (quiral)",
        "Isomería óptica",
        "Enantiómeros vs Diastereómeros",
        "Configuración R/S"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Apunte", fontSize = 18.sp, color = DarkGreen, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = DarkGreen)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Compartir */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir", tint = DarkGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundBeige)
            )
        },
        containerColor = BackgroundBeige
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = noteTitle,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusTag(status = if (noteTitle.contains("Estereoquímica")) NoteStatus.WEAK else NoteStatus.SOLID)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Clase 5  ·  Química Orgánica", color = TextGray, fontSize = 14.sp)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Resumen de la IA",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
            ) {
                Text(
                    text = fullContent,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 16.sp,
                    color = TextDark,
                    lineHeight = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Conceptos clave",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            keyPoints.forEach { point ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(DarkGreen, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = point, color = TextDark, fontSize = 16.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { /* Generar cuestionario */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                Text("Practicar con Cuestionario", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
