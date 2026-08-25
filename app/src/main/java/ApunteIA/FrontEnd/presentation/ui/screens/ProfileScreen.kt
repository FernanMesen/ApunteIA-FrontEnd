package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.background
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
    onThemeToggle: (Boolean) -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ProfileTopBar(onBack = onBack)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
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
                    ConfigSection(isDarkMode = isDarkMode, onThemeToggle = onThemeToggle)
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp)) // Espacio para el botón de abajo
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                LogoutButton(onClick = onLogout)
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            StatItem("Notas capturadas", "6")
            HorizontalDivider(color = Color(0xFFF5F5F5).copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
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
fun ConfigSection(isDarkMode: Boolean, onThemeToggle: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "CONFIGURACIÓN",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextGray,
            modifier = Modifier.padding(start = 4.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column {
                ConfigItem("Administrar materias", trailing = { Icon(Icons.Default.KeyboardArrowRight, null, tint = TextGray, modifier = Modifier.size(20.dp)) })
                HorizontalDivider(color = Color(0xFFF5F5F5).copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                ConfigItem("Historial de estudio", trailing = { Icon(Icons.Default.KeyboardArrowRight, null, tint = TextGray, modifier = Modifier.size(20.dp)) })
                HorizontalDivider(color = Color(0xFFF5F5F5).copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                ConfigItem("Apariencia", trailing = { AppearanceToggle(isDarkMode, onThemeToggle) })
                HorizontalDivider(color = Color(0xFFF5F5F5).copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                ConfigItem("Acceso a cámara", trailing = { Icon(Icons.Default.Check, null, tint = DarkGreen, modifier = Modifier.size(20.dp)) })
                HorizontalDivider(color = Color(0xFFF5F5F5).copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                ConfigItem("Privacidad", trailing = { Icon(Icons.Default.KeyboardArrowRight, null, tint = TextGray, modifier = Modifier.size(20.dp)) })
                HorizontalDivider(color = Color(0xFFF5F5F5).copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                ConfigItem("Exportar mis datos", trailing = { Icon(Icons.Default.KeyboardArrowRight, null, tint = TextGray, modifier = Modifier.size(20.dp)) })
                HorizontalDivider(color = Color(0xFFF5F5F5).copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                ConfigItem("Cambiar contraseña", trailing = { Icon(Icons.Default.KeyboardArrowRight, null, tint = TextGray, modifier = Modifier.size(20.dp)) })
            }
        }
    }
}

@Composable
fun ConfigItem(label: String, trailing: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO */ }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
        trailing()
    }
}

@Composable
fun AppearanceToggle(isDarkMode: Boolean, onThemeToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .background(Color(0xFFF5F5F5).copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (!isDarkMode) DarkGreen else Color.Transparent)
                .clickable { onThemeToggle(false) }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Claro",
                color = if (!isDarkMode) Color.White else TextGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isDarkMode) DarkGreen else Color.Transparent)
                .clickable { onThemeToggle(true) }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Oscuro",
                color = if (isDarkMode) Color.White else TextGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = LightRed,
            contentColor = StatusRed
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
