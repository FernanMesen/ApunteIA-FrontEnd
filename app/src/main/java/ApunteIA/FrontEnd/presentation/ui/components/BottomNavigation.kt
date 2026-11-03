package ApunteIA.FrontEnd.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.R

@Composable
fun CustomBottomNavigation(
    currentRoute: String,
    onInicio: () -> Unit,
    onNotas: () -> Unit,
    onAuditoria: () -> Unit,
    onReforzar: () -> Unit,
    onCursos: () -> Unit = {},
    isAdmin: Boolean = LocalCurrentUser.current?.isAdmin == true
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        tonalElevation = 8.dp,
        modifier = Modifier.height(70.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Default.Home,
                label = stringResource(R.string.nav_home),
                isSelected = currentRoute == "Inicio",
                onClick = onInicio
            )

            if (isAdmin) {
                NavItem(
                    icon = Icons.Default.School,
                    label = stringResource(R.string.admin_courses_title),
                    isSelected = currentRoute == "Cursos" || currentRoute == "Administración",
                    onClick = onCursos
                )
            } else {
                NavItem(
                    icon = Icons.Default.Description,
                    label = stringResource(R.string.nav_notes),
                    isSelected = currentRoute == "Notas",
                    onClick = onNotas
                )
                
                Spacer(modifier = Modifier.width(48.dp))
                
                NavItem(
                    icon = Icons.Default.Analytics,
                    label = stringResource(R.string.nav_audit),
                    isSelected = currentRoute == "Auditoría",
                    onClick = onAuditoria
                )
                NavItem(
                    icon = Icons.Default.HistoryEdu,
                    label = stringResource(R.string.nav_reinforce),
                    isSelected = currentRoute == "Reforzar",
                    onClick = onReforzar
                )
            }
        }
    }
}

@Composable
fun NavItem(icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    val isDark = MaterialTheme.colorScheme.primary == Color(0xFF25344F) // SpaceCadet
    
    IconButton(onClick = onClick, modifier = Modifier.size(48.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (isSelected) {
                    if (isDark) Color.White else MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = if (isSelected) {
                    if (isDark) Color.White else MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
