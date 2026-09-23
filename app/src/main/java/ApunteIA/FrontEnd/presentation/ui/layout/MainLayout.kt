package ApunteIA.FrontEnd.presentation.ui.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ApunteIA.FrontEnd.presentation.ui.components.CustomBottomNavigation
import ApunteIA.FrontEnd.presentation.ui.theme.DarkGreen
import ApunteIA.FrontEnd.presentation.ui.theme.SpaceCadet

/**
 * Layout compartido por las pantallas principales de la app (Inicio, Notas, ...):
 * arma el [Scaffold] con la barra inferior de navegación y el botón flotante
 * de cámara, para no repetirlo pantalla por pantalla.
 */
@Composable
fun MainLayout(
    currentRoute: String,
    onInicio: () -> Unit,
    onNotas: () -> Unit,
    onAuditoria: () -> Unit,
    onReforzar: () -> Unit,
    onCameraClick: () -> Unit = {},
    showFab: Boolean = true,
    topBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet

    Scaffold(
        topBar = topBar,
        snackbarHost = snackbarHost,
        bottomBar = {
            CustomBottomNavigation(
                currentRoute = currentRoute,
                onInicio = onInicio,
                onNotas = onNotas,
                onAuditoria = onAuditoria,
                onReforzar = onReforzar
            )
        },
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = onCameraClick,
                    containerColor = if (isDark) Color(0xFF394867) else MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(64.dp)
                        .offset(y = 40.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Camera", modifier = Modifier.size(32.dp))
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = MaterialTheme.colorScheme.background,
        content = content
    )
}
