package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.presentation.ui.theme.*
import ApunteIA.FrontEnd.presentation.ui.layout.MainLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userName: String = "Maya",
    streakDays: Int = 12,
    progress: Int = 53,
    notesCount: Int = 6,
    pendingTopics: Int = 6,
    quizzesDone: Int = 9,
    onInicio: () -> Unit,
    onNotas: () -> Unit,
    onAuditoria: () -> Unit,
    onReforzar: () -> Unit,
    onCameraClick: () -> Unit = {}
) {
    MainLayout(
        currentRoute = "Inicio",
        onInicio = onInicio,
        onNotas = onNotas,
        onAuditoria = onAuditoria,
        onReforzar = onReforzar,
        onCameraClick = onCameraClick,
        topBar = { TopHeader() }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                GreetingSection(userName, streakDays)
            }
            item {
                StatsCard(progress, notesCount, pendingTopics, quizzesDone)
            }
            item {
                CycleSection()
            }
            item {
                RecentActivitySection()
            }
            item {
                Spacer(modifier = Modifier.height(80.dp)) // Extra space for FAB/Nav
            }
        }
    }
}

@Composable
fun TopHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ApuntesIA",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEBEBE4)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = DarkGreen,
                    modifier = Modifier.size(22.dp)
                )
                // Red badge
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(StatusRed)
                        .align(Alignment.TopEnd)
                        .offset(x = (-4).dp, y = 4.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(DarkGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "M",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun GreetingSection(userName: String, streakDays: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Buenas tardes",
            fontSize = 16.sp,
            color = TextGray
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = userName,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )

            Surface(
                color = Color.White,
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(DarkGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Racha de $streakDays días",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextGray
                    )
                }
            }
        }
    }
}

@Composable
fun StatsCard(progress: Int, notesCount: Int, pendingTopics: Int, quizzesDone: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = Color(0xFFEEEEEE),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = DarkGreen,
                        startAngle = -90f,
                        sweepAngle = (progress / 100f) * 360f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$progress%", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkGreen)
                    Text(text = "cubierto", fontSize = 10.sp, color = TextGray)
                }
            }
            Spacer(modifier = Modifier.width(24.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StatLine("Notas capturadas", notesCount.toString(), DarkGreen)
                StatLine("Temas pendientes", pendingTopics.toString(), StatusRed)
                StatLine("Cuestionarios hechos", quizzesDone.toString(), DarkGreen)
            }
        }
    }
}

@Composable
fun StatLine(label: String, value: String, valueColor: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextGray)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable
fun CycleSection() {
    Column {
        Text(text = "TU CICLO", style = MaterialTheme.typography.titleSmall, color = TextGray, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CycleItem("1", "Capturar", false)
            CycleConnector(Modifier.weight(1f))
            CycleItem("2", "Organizar", false)
            CycleConnector(Modifier.weight(1f))
            CycleItem("3", "Auditar", false)
            CycleConnector(Modifier.weight(1f))
            CycleItem("4", "Reforzar", true)
        }
    }
}

@Composable
fun CycleItem(number: String, label: String, isHighlighted: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(60.dp)) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isHighlighted) DarkGreen else Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontWeight = FontWeight.Bold,
                color = if (isHighlighted) Color.White else DarkGreen,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (isHighlighted) DarkGreen else TextGray,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
    }
}

@Composable
fun CycleConnector(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(1.dp)
            .background(Color(0xFFE0E0E0))
            .offset(y = (-8).dp)
    )
}

@Composable
fun RecentActivitySection() {
    Column {
        Text(text = "ACTIVIDAD RECIENTE", style = MaterialTheme.typography.titleSmall, color = TextGray, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ActivityItem("Escaneadas 3 páginas", "Mecanismos de Reacción", "Hace 2h")
                HorizontalDivider(color = Color(0xFFF5F5F5))
                ActivityItem("Cuestionario completado", "Estereoquímica (4/5)", "Ayer")
                HorizontalDivider(color = Color(0xFFF5F5F5))
                ActivityItem("Auditoría actualizada", "2 brechas nuevas", "Hace 2 días")
                HorizontalDivider(color = Color(0xFFF5F5F5))
                ActivityItem("Escaneadas 2 páginas", "Transporte en la Membrana Celular", "Hace 3 días")
            }
        }
    }
}

@Composable
fun ActivityItem(title: String, subtitle: String, time: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = DarkGreen)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextGray)
        }
        Text(text = time, style = MaterialTheme.typography.bodySmall, color = TextGray)
    }
}

