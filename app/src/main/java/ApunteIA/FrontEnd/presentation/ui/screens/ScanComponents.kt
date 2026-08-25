package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.presentation.ui.theme.DarkGreen
import ApunteIA.FrontEnd.presentation.ui.theme.LightGreen
import ApunteIA.FrontEnd.presentation.ui.theme.TextGray

/**
 * Encabezado común de las 4 pantallas del flujo de escaneo: botón de cerrar
 * (X) a la izquierda, título opcional al centro/izquierda y un slot libre
 * a la derecha (usado por el badge de páginas capturadas).
 */
@Composable
fun ScanTopBar(
    title: String? = null,
    onClose: () -> Unit,
    trailingContent: @Composable () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFEDEDED), CircleShape)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = DarkGreen)
        }

        if (title != null) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen,
                modifier = Modifier.weight(1f).padding(start = 12.dp)
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        trailingContent()
    }
}

/** Chip para elegir la materia (Química Orgánica, Biología Celular, ...). */
@Composable
fun SubjectChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) LightGreen else Color.White,
        border = BorderStroke(1.dp, if (isSelected) DarkGreen else Color(0xFFE0E0E0))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            color = if (isSelected) DarkGreen else TextGray,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

/** Chip para elegir el tipo de material (notas manuscritas, diapositivas, ...). */
@Composable
fun MaterialTypeChip(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, if (isSelected) DarkGreen else Color(0xFFE0E0E0))
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (isSelected) DarkGreen else TextGray,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

/** Pequeña insignia circular/pill usada para mostrar el número de páginas capturadas. */
@Composable
fun CountBadge(count: Int) {
    Surface(
        shape = CircleShape,
        color = Color(0xFFEDEDED)
    ) {
        Text(
            text = "$count",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = DarkGreen,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}