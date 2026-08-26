package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.presentation.ui.layout.MainLayout
import ApunteIA.FrontEnd.presentation.ui.components.TopHeader
import ApunteIA.FrontEnd.presentation.ui.theme.DarkGreen
import ApunteIA.FrontEnd.presentation.ui.theme.LightGreen
import ApunteIA.FrontEnd.presentation.ui.theme.SpaceCadet
import ApunteIA.FrontEnd.presentation.viewmodel.NotificationViewModel

/**
 * Pantalla "Reforzar": punto de entrada para practicar con quizzes generados
 * a partir de las notas más débiles del usuario.
 */
@Composable
fun ReinforcementScreen(
    onInicio: () -> Unit = {},
    onNotas: () -> Unit = {},
    onAuditoria: () -> Unit = {},
    onReforzar: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onStartQuiz: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet

    MainLayout(
        currentRoute = "Reforzar",
        onInicio = onInicio,
        onNotas = onNotas,
        onAuditoria = onAuditoria,
        onReforzar = onReforzar,
        onCameraClick = onCameraClick,
        topBar = { 
            TopHeader(
                onProfileClick = onProfileClick
            ) 
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0xFF1E2532) else LightGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = if (isDark) Color.White else DarkGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Repaso Mixto",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Configurar práctica",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
            
            item {
                ReinforcementConfigSection("Preguntas") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SegmentedOption("5", isSelected = true)
                        SegmentedOption("10", isSelected = false)
                        SegmentedOption("15", isSelected = false)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            item {
                ReinforcementConfigSection("Tipo de pregunta") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SegmentedOption("Mixto", isSelected = true)
                            SegmentedOption("Opción múltiple", isSelected = false)
                            SegmentedOption("Tarjetas", isSelected = false)
                        }
                        SegmentedOption("Respuesta corta", isSelected = false)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            item {
                ReinforcementConfigSection("Dificultad") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SegmentedOption("Fácil", isSelected = false)
                        SegmentedOption("Media", isSelected = true)
                        SegmentedOption("Difícil", isSelected = false)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            item {
                ReinforcementConfigSection("Modo") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SegmentedOption("Práctica", isSelected = true, modifier = Modifier.weight(1f))
                        SegmentedOption("Examen", isSelected = false, modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            item {
                ToggleOption("Solo temas débiles")
                Spacer(modifier = Modifier.height(12.dp))
                ToggleOption("Solo errores previos")
                Spacer(modifier = Modifier.height(12.dp))
                ToggleOption("Temporizador")
                
                Spacer(modifier = Modifier.height(40.dp))
                
                Button(
                    onClick = onStartQuiz,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Comenzar repaso", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun ReinforcementConfigSection(label: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
fun SegmentedOption(text: String, isSelected: Boolean, modifier: Modifier = Modifier) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    
    Surface(
        modifier = modifier.clickable { /* TODO */ },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) {
            if (isDark) Color(0xFF394867) else DarkGreen
        } else {
            if (isDark) Color(0xFF1E2532) else Color(0xFFF5F5F5)
        }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ToggleOption(text: String) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color.White),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF252B3B) else Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            // Estilo de switch manual para el diseño
            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isDark) Color(0xFF1E2532) else Color(0xFFE0E0E0)),
                contentAlignment = Alignment.CenterStart
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isDark) Color(0xFF9BA4B5) else Color.White)
                )
            }
        }
    }
}
