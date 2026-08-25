package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ApunteIA.FrontEnd.presentation.model.CapturedPage
import ApunteIA.FrontEnd.presentation.ui.theme.BackgroundBeige
import ApunteIA.FrontEnd.presentation.ui.theme.DarkGreen
import ApunteIA.FrontEnd.presentation.ui.theme.StatusRed
import ApunteIA.FrontEnd.presentation.ui.theme.TextGray
import ApunteIA.FrontEnd.presentation.viewmodel.ScanViewModel

@Composable
fun ScanPagesScreen(
    viewModel: ScanViewModel,
    onClose: () -> Unit,
    onAddMorePages: () -> Unit,
    onConfirm: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBeige)
            .padding(horizontal = 20.dp)
    ) {
        ScanTopBar(
            title = "Páginas capturadas",
            onClose = onClose,
            trailingContent = { CountBadge(count = state.capturedPages.size) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.capturedPages, key = { it.id }) { page ->
                val index = state.capturedPages.indexOf(page)
                CapturedPageRow(
                    page = page,
                    pageNumber = index + 1,
                    canMoveUp = index > 0,
                    canMoveDown = index < state.capturedPages.lastIndex,
                    onMoveUp = { viewModel.movePage(page.id, -1) },
                    onMoveDown = { viewModel.movePage(page.id, 1) },
                    onRemove = { viewModel.removePage(page.id) }
                )
            }

            item { Spacer(modifier = Modifier.height(4.dp)) }
        }

        OutlinedButton(
            onClick = onAddMorePages,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkGreen)
        ) {
            Text("Agregar más páginas", fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onConfirm,
            enabled = state.hasCapturedPages,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 20.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkGreen,
                disabledContainerColor = DarkGreen.copy(alpha = 0.4f)
            )
        ) {
            Text("Confirmar y procesar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun CapturedPageRow(
    page: CapturedPage,
    pageNumber: Int,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Miniatura de la página (foto real capturada por la cámara)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
            ) {
                AsyncImage(
                    model = page.imageFile,
                    contentDescription = "Página $pageNumber",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                )
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFEDEDED),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(
                        text = "$pageNumber",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = DarkGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Página $pageNumber",
                    color = DarkGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PageToolIcon(Icons.Default.Crop, "Recortar")
                    PageToolIcon(Icons.Default.Refresh, "Rotar")
                    PageToolIcon(Icons.Default.WbSunny, "Brillo/contraste")
                    PageToolIcon(Icons.Default.CameraAlt, "Volver a capturar")
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row {
                    ReorderIcon(Icons.Default.KeyboardArrowUp, enabled = canMoveUp, onClick = onMoveUp)
                    ReorderIcon(Icons.Default.KeyboardArrowDown, enabled = canMoveDown, onClick = onMoveDown)
                }
                Spacer(modifier = Modifier.height(4.dp))
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFFFDECEC), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Eliminar página", tint = StatusRed, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun PageToolIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, description: String) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .background(Color(0xFFF5F5F0), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = description, tint = DarkGreen, modifier = Modifier.size(15.dp))
    }
}

@Composable
private fun ReorderIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, enabled: Boolean, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(22.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (enabled) TextGray else Color(0xFFE0E0E0),
            modifier = Modifier.size(18.dp)
        )
    }
}