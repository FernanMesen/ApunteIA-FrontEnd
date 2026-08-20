package ApunteIA.FrontEnd.presentation.ui.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ApunteIA.FrontEnd.presentation.ui.components.CustomBottomNavigation
import ApunteIA.FrontEnd.presentation.ui.theme.BackgroundBeige
import ApunteIA.FrontEnd.presentation.ui.theme.DarkGreen

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
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = topBar,
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
                    containerColor = DarkGreen,
                    contentColor = Color.White,
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
        containerColor = BackgroundBeige,
        content = content
    )
}
