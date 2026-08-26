package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import ApunteIA.FrontEnd.presentation.ui.theme.*

@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {},
    isDarkMode: Boolean = false,
    onThemeToggle: (Boolean) -> Unit = {},
    onManageSubjects: () -> Unit = {},
    onStudyHistory: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onCameraAccess: () -> Unit = {},
    onPrivacy: () -> Unit = {},
    onExportData: () -> Unit = {},
    onChangePassword: () -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val isThemeDark = MaterialTheme.colorScheme.primary == SpaceCadet

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = if (isThemeDark) Color(0xFF252E3E) else Color.White,
            title = {
                Text(
                    text = "¿Eliminar tu cuenta?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "Todas las notas, materias y progreso se eliminarán permanentemente.",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isThemeDark) Color(0xFF3B2525) else StatusRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirmar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false },
                    border = BorderStroke(1.dp, if (isThemeDark) Color(0xFF394867) else Color(0xFFEEEEEE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Cancelar", 
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ProfileTopBar(onBack = onBack)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                ProfileHeader()
            }

            item {
                StatsSection()
            }

            item {
                ConfigSection(
                    isDarkMode = isDarkMode,
                    onThemeToggle = onThemeToggle,
                    onManageSubjects = onManageSubjects,
                    onStudyHistory = onStudyHistory,
                    onNotificationsClick = onNotificationsClick,
                    onCameraAccess = onCameraAccess,
                    onPrivacy = onPrivacy,
                    onExportData = onExportData,
                    onChangePassword = onChangePassword
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                LogoutButton(onClick = onLogout)
            }

            item {
                Text(
                    text = "Eliminar cuenta",
                    color = if (MaterialTheme.colorScheme.primary == SpaceCadet) Color(0xFFC07B7B) else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .clickable { showDeleteDialog = true }
                )
            }
        }
    }
}

@Composable
fun ProfileTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        
        Text(
            text = "Perfil",
            modifier = Modifier.weight(1f).padding(end = 40.dp),
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun ProfileHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "M",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Maya Torres",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Text(
            text = "maya.torres@email.com",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StatsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            StatItem("Notas capturadas", "6")
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), modifier = Modifier.padding(horizontal = 16.dp))
            StatItem("Cuestionarios hechos", "9")
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun ConfigSection(
    isDarkMode: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    onManageSubjects: () -> Unit,
    onStudyHistory: () -> Unit,
    onNotificationsClick: () -> Unit,
    onCameraAccess: () -> Unit,
    onPrivacy: () -> Unit,
    onExportData: () -> Unit,
    onChangePassword: () -> Unit
) {
    val isThemeDark = MaterialTheme.colorScheme.primary == SpaceCadet
    
    Column {
        Text(
            text = "CONFIGURACIÓN",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isThemeDark) Color(0xFF9BA4B5) else TextGray,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = if (isThemeDark) BorderStroke(1.dp, Color(0xFF394867)) else null
        ) {
            Column {
                ConfigItem(
                    label = "Administrar materias",
                    onClick = onManageSubjects,
                    trailing = { Icon(Icons.Default.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp)) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), thickness = 0.5.dp)
                ConfigItem(
                    label = "Historial de estudio",
                    onClick = onStudyHistory,
                    trailing = { Icon(Icons.Default.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp)) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), thickness = 0.5.dp)
                ConfigItem(
                    label = "Notificaciones",
                    onClick = onNotificationsClick,
                    trailing = { Icon(Icons.Default.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp)) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), thickness = 0.5.dp)
                ConfigItem(
                    label = "Apariencia",
                    trailing = { AppearanceToggle(isDarkMode, onThemeToggle) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), thickness = 0.5.dp)
                ConfigItem(
                    label = "Acceso a cámara",
                    onClick = onCameraAccess,
                    trailing = { Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp)) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), thickness = 0.5.dp)
                ConfigItem(
                    label = "Privacidad",
                    onClick = onPrivacy,
                    trailing = { Icon(Icons.Default.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp)) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), thickness = 0.5.dp)
                ConfigItem(
                    label = "Exportar mis datos",
                    onClick = onExportData,
                    trailing = { Icon(Icons.Default.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp)) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), thickness = 0.5.dp)
                ConfigItem(
                    label = "Cambiar contraseña",
                    onClick = onChangePassword,
                    trailing = { Icon(Icons.Default.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp)) }
                )
            }
        }
    }
}

@Composable
fun ConfigItem(label: String, onClick: () -> Unit = {}, trailing: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label, 
            fontSize = 15.sp, 
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        trailing()
    }
}

@Composable
fun AppearanceToggle(isDarkMode: Boolean, onThemeToggle: (Boolean) -> Unit) {
    val isThemeDark = MaterialTheme.colorScheme.primary == SpaceCadet
    
    Row(
        modifier = Modifier
            .background(if (isThemeDark) Color(0xFF1E2532) else Color(0xFFF5F5F5), RoundedCornerShape(20.dp))
            .border(0.5.dp, if (isThemeDark) Color(0xFF394867) else Color.Transparent, RoundedCornerShape(20.dp))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (!isDarkMode) (if (isThemeDark) Color(0xFF394867) else DarkGreen) else Color.Transparent)
                .clickable { onThemeToggle(false) }
                .padding(horizontal = 16.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Claro",
                color = if (!isDarkMode) Color.White else (if (isThemeDark) Color(0xFF9BA4B5) else TextGray),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isDarkMode) (if (isThemeDark) Color(0xFF394867) else DarkGreen) else Color.Transparent)
                .clickable { onThemeToggle(true) }
                .padding(horizontal = 16.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Oscuro",
                color = if (isDarkMode) Color.White else (if (isThemeDark) Color(0xFF9BA4B5) else TextGray),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isDark) Color(0xFF3B2525) else LightRed,
            contentColor = if (isDark) Color(0xFFC07B7B) else StatusRed
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = "Cerrar sesión",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
