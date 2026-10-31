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
import androidx.compose.material3.MaterialTheme
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
import ApunteIA.FrontEnd.presentation.ui.theme.SpaceCadet
import ApunteIA.FrontEnd.presentation.ui.theme.TextGray

/**
 * Chip de categoría usado en el filtro de la lista de notas.
 */
@Composable
fun FilterChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet

    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) {
            if (isDark) Color(0xFF394867) else Color(0xFFEBEBE4)
        } else {
            if (isDark) Color(0xFF252B3B) else Color.White
        },
        border = BorderStroke(1.dp, if (isSelected) (if (isDark) Color.White else DarkGreen) else Color.Transparent)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isSelected) {
                if (isDark) Color.White else DarkGreen
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
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
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    modifier = Modifier.weight(1f, fill = false).padding(end = 8.dp)
                )
                StatusTag(status = note.status)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = listOf(
                    note.className,
                    if (note.classNumber > 0) "Clase ${note.classNumber}" else "",
                    if (note.date.isNotBlank()) "Escaneado ${note.date}" else ""
                ).filter { it.isNotBlank() }.joinToString("  ·  "),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = note.preview,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    val isWeak = status == NoteStatus.WEAK

    val backgroundColor = if (isWeak) {
        if (isDark) Color(0xFF3B2E2E) else Color(0xFFFDECEC)
    } else {
        if (isDark) Color(0xFF2E3B3B) else Color(0xFFE0F2F1)
    }

    val textColor = if (isWeak) {
        if (isDark) Color(0xFFD08D74) else Color(0xFFE57373)
    } else {
        if (isDark) Color(0xFF9BA4B5) else Color(0xFF4DB6AC)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = status.label, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}