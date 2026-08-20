package ApunteIA.FrontEnd.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.ui.theme.*
import ApunteIA.FrontEnd.components.CustomBottomNavigation

data class NoteItem(
    val title: String,
    val className: String,
    val classNumber: Int,
    val date: String,
    val preview: String,
    val status: String
)

@Composable
fun NotesScreen(
    onInicio: () -> Unit = {},
    onNotas: () -> Unit = {},
    onAuditoria: () -> Unit = {},
    onReforzar: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onNoteClick: (String) -> Unit = {}
) {
    val categories = listOf("Todos", "Química Orgánica", "Biología Celular", "Cálculo")
    var selectedCategory by remember { mutableStateOf("Todos") }
    var searchQuery by remember { mutableStateOf("") }

    val notes = listOf(
        NoteItem(
            "Estereoquímica",
            "Química Orgánica",
            5,
            "Escaneado 4 ago",
            "Los centros quirales surgen cuando un carbono se une a cuatro grupos diferentes. Los enantiómeros son imágenes especulares no superponibles...",
            "Débil"
        ),
        NoteItem(
            "Alcanos y Nomenclatura",
            "Química Orgánica",
            3,
            "Escaneado 28 jul",
            "La nomenclatura IUPAC sigue la cadena de carbono más larga, numerada para dar a los sustituyentes las posiciones más bajas.",
            "Sólido"
        ),
        NoteItem(
            "Química Ácido-Base",
            "Química Orgánica",
            2,
            "Escaneado 22 jul",
            "Los ácidos de Bronsted-Lowry donan protones; la fuerza se correlaciona con la estabilidad de la base conjugada.",
            "Sólido"
        ),
        NoteItem(
            "Ciclo Celular",
            "Biología Celular",
            4,
            "Escaneado 5 ago",
            "La interfase es la fase más larga del ciclo celular, donde la célula crece y copia su ADN antes de la mitosis.",
            "Débil"
        ),
        NoteItem(
            "Límites e Infinito",
            "Cálculo",
            1,
            "Escaneado 10 ago",
            "El concepto de límite es fundamental para el cálculo diferencial e integral...",
            "Sólido"
        )
    )

    // Lógica de filtrado
    val filteredNotes = notes.filter { note ->
        val matchesSearch = note.title.contains(searchQuery, ignoreCase = true) ||
                note.preview.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "Todos" || note.className == selectedCategory
        
        matchesSearch && matchesCategory
    }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                currentRoute = "Notas",
                onInicio = onInicio,
                onNotas = onNotas,
                onAuditoria = onAuditoria,
                onReforzar = onReforzar
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCameraClick,
                containerColor = DarkGreen,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .size(64.dp)
                    .offset(y = 40.dp)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Camera", modifier = Modifier.size(32.dp))
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = BackgroundBeige
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Notas",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )
                Text(
                    text = "Estructuradas por IA a partir de tus escaneos",
                    fontSize = 14.sp,
                    color = TextGray
                )
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar notas", color = TextGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGray) },
                    singleLine = true
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { category ->
                        FilterChip(
                            text = category,
                            isSelected = selectedCategory == category,
                            onClick = { selectedCategory = category }
                        )
                    }
                }
            }

            if (filteredNotes.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text("No se encontraron notas", color = TextGray)
                    }
                }
            } else {
                items(filteredNotes) { note ->
                    NoteCard(note, onClick = { onNoteClick(note.title) })
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun FilterChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color(0xFFEBEBE4) else Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) DarkGreen else Color(0xFFEEEEEE))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isSelected) DarkGreen else TextGray,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
fun NoteCard(note: NoteItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )
                StatusTag(status = note.status)
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row {
                Text(text = "Clase ${note.classNumber}", fontSize = 12.sp, color = TextGray)
                Text(text = "  ·  ", fontSize = 12.sp, color = TextGray)
                Text(text = note.date, fontSize = 12.sp, color = TextGray)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = note.preview,
                fontSize = 14.sp,
                color = TextGray,
                lineHeight = 20.sp,
                maxLines = 3
            )
        }
    }
}

@Composable
fun StatusTag(status: String) {
    val backgroundColor = if (status == "Débil") Color(0xFFFDECEC) else Color(0xFFE0F2F1)
    val textColor = if (status == "Débil") Color(0xFFE57373) else Color(0xFF4DB6AC)
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = status, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

