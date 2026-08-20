package ApunteIA.FrontEnd.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.ui.theme.DarkGreen
import ApunteIA.FrontEnd.ui.theme.TextGray

@Composable
fun CustomBottomNavigation(
    currentRoute: String,
    onInicio: () -> Unit,
    onNotas: () -> Unit,
    onAuditoria: () -> Unit,
    onReforzar: () -> Unit
) {
    BottomAppBar(
        containerColor = Color.White,
        contentColor = DarkGreen,
        tonalElevation = 8.dp,
        modifier = Modifier.height(70.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(Icons.Default.Home, "Inicio", currentRoute == "Inicio", onInicio)
            NavItem(Icons.Default.Description, "Notas", currentRoute == "Notas", onNotas)
            
            Spacer(modifier = Modifier.width(48.dp))
            
            NavItem(Icons.Default.Analytics, "Auditoría", currentRoute == "Auditoría", onAuditoria)
            NavItem(Icons.Default.HistoryEdu, "Reforzar", currentRoute == "Reforzar", onReforzar)
        }
    }
}

@Composable
fun NavItem(icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(48.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (isSelected) DarkGreen else TextGray,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                fontSize = 8.sp,
                color = if (isSelected) DarkGreen else TextGray,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
