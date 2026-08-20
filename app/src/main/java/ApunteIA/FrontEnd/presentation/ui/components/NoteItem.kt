package ApunteIA.FrontEnd.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.model.NoteStatus
import ApunteIA.FrontEnd.presentation.ui.theme.DarkGreen
import ApunteIA.FrontEnd.presentation.ui.theme.TextGray

/**
 * Chip de categoría usado en el filtro de la lista de notas.
 */
@Composable
fun FilterChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color(0xFFEBEBE4) else Color.White,
        border = BorderStroke(1.dp, if (isSelected) DarkGreen else Color(0xFFEEEEEE))
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

/**
 * Tarjeta que representa una nota dentro de la lista (equivalente al
 * "TaskItem" del ejemplo, pero para el dominio de Notas).
 */
@Composable
fun NoteCard(note: Note, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFF0F0F0))
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

/**
 * Etiqueta de estado (Débil / Sólido) de una nota.
 */
@Composable
fun StatusTag(status: NoteStatus) {
    val isWeak = status == NoteStatus.WEAK
    val backgroundColor = if (isWeak) Color(0xFFFDECEC) else Color(0xFFE0F2F1)
    val textColor = if (isWeak) Color(0xFFE57373) else Color(0xFF4DB6AC)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = status.label, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
