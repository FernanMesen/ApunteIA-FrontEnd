package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.presentation.ui.theme.BackgroundBeige
import ApunteIA.FrontEnd.presentation.ui.theme.DarkAccent
import ApunteIA.FrontEnd.presentation.ui.theme.DarkBorder
import ApunteIA.FrontEnd.presentation.ui.theme.DarkButton
import ApunteIA.FrontEnd.presentation.ui.theme.DarkGreen
import ApunteIA.FrontEnd.presentation.ui.theme.DarkSoftFill
import ApunteIA.FrontEnd.presentation.ui.theme.LightGreen
import ApunteIA.FrontEnd.presentation.ui.theme.TextGray
import ApunteIA.FrontEnd.presentation.ui.theme.lightOr

/*
 * Colores del flujo de escaneo (Nuevo escaneo → Cámara → Páginas → Procesando → Revisar).
 * En claro son exactamente los de antes (BackgroundBeige, DarkGreen, blanco...);
 * en oscuro, los del tema y los tonos que ya usa el resto de la app.
 */

/** Fondo de pantalla (BackgroundBeige en claro). */
@Composable
fun scanBackground(): Color = lightOr(BackgroundBeige, MaterialTheme.colorScheme.background)

/** Títulos y textos principales (DarkGreen en claro). */
@Composable
fun scanText(): Color = lightOr(DarkGreen, MaterialTheme.colorScheme.onSurface)

/** Íconos, enlaces de acción, progreso y bordes de selección (DarkGreen en claro). */
@Composable
fun scanAccent(): Color = lightOr(DarkGreen, DarkAccent)

/** Fondo de botones principales (DarkGreen en claro). */
@Composable
fun scanButton(): Color = lightOr(DarkGreen, DarkButton)

/** Tarjetas, campos y chips (blanco en claro). */
@Composable
fun scanCard(): Color = lightOr(Color.White, MaterialTheme.colorScheme.surfaceVariant)

/** Texto secundario (TextGray en claro). */
@Composable
fun scanMuted(): Color = lightOr(TextGray, MaterialTheme.colorScheme.onSurfaceVariant)

/** Bordes; [light] es el gris que tenía cada pantalla. */
@Composable
fun scanBorder(light: Color = Color(0xFFE0E0E0)): Color = lightOr(light, DarkBorder)

/** Fondos suaves de botones redondos, insignias y chips de estado. */
@Composable
fun scanSoftFill(light: Color = Color(0xFFEDEDED)): Color = lightOr(light, DarkSoftFill)

/**
 * Encabezado común de las 4 pantallas del flujo de escaneo.
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
                .background(scanSoftFill(), CircleShape)
        ) {
            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close), tint = scanText())
        }

        if (title != null) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = scanText(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(start = 12.dp, end = 8.dp)
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        trailingContent()
    }
}

/** Chip para elegir la materia. */
@Composable
fun SubjectChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) lightOr(LightGreen, DarkButton) else scanCard(),
        border = BorderStroke(1.dp, if (isSelected) scanAccent() else scanBorder())
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            color = if (isSelected) scanText() else scanMuted(),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1
        )
    }
}

/** Chip para elegir el tipo de material. */
@Composable
fun MaterialTypeChip(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) lightOr(Color.White, DarkButton) else scanCard(),
        border = BorderStroke(1.dp, if (isSelected) scanAccent() else scanBorder())
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Media pantalla de ancho: un texto largo ("Handwritten notes") baja a 2 líneas centradas.
            Text(
                text = text,
                color = if (isSelected) scanText() else scanMuted(),
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** Pequeña insignia circular/pill usada para mostrar el número de páginas capturadas. */
@Composable
fun CountBadge(count: Int) {
    Surface(
        shape = CircleShape,
        color = scanSoftFill()
    ) {
        Text(
            text = "$count",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = scanText(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
