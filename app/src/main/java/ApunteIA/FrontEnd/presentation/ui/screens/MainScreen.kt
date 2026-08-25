package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.domain.model.RecentActivity
import ApunteIA.FrontEnd.presentation.ui.theme.*
import ApunteIA.FrontEnd.presentation.ui.layout.MainLayout
import ApunteIA.FrontEnd.presentation.ui.components.TopHeader
import ApunteIA.FrontEnd.presentation.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onInicio: () -> Unit,
    onNotas: () -> Unit,
    onAuditoria: () -> Unit,
    onReforzar: () -> Unit,
    onCameraClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val userStats by viewModel.userStats.collectAsState()
    val recentActivity by viewModel.recentActivity.collectAsState()

    MainLayout(
        currentRoute = "Inicio",
        onInicio = onInicio,
        onNotas = onNotas,
        onAuditoria = onAuditoria,
        onReforzar = onReforzar,
        onCameraClick = onCameraClick,
        topBar = { TopHeader(onProfileClick = onProfileClick) }
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
                userStats?.let { 
                    GreetingSection(it.userName, it.streakDays)
                }
            }
            
            item {
                userStats?.let { stats ->
                    StatsCard(
                        progress = stats.progressPercent,
                        notesCount = stats.notesCount,
                        pendingTopics = stats.pendingTopicsCount,
                        quizzesDone = stats.quizzesDoneCount
                    )
                }
            }
            
            item {
                CycleSection(
                    onOrganizarClick = onNotas,
                    onReforzarClick = onReforzar
                )
            }
            
            item {
                RecentActivitySection(recentActivity)
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp)) // Extra space for FAB/Nav
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
fun CycleSection(onOrganizarClick: () -> Unit, onReforzarClick: () -> Unit) {
    Column {
        Text(text = "TU CICLO", style = MaterialTheme.typography.titleSmall, color = TextGray, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CycleItem("1", "Capturar", false)
            CycleConnector(Modifier.weight(1f))
            CycleItem("2", "Organizar", false, onClick = onOrganizarClick)
            CycleConnector(Modifier.weight(1f))
            CycleItem("3", "Auditar", false)
            CycleConnector(Modifier.weight(1f))
            CycleItem("4", "Reforzar", true, onClick = onReforzarClick)
        }
    }
}

@Composable
fun CycleItem(number: String, label: String, isHighlighted: Boolean, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(60.dp)
            .clickable { onClick() }
    ) {
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
fun RecentActivitySection(activities: List<RecentActivity>) {
    Column {
        Text(text = "ACTIVIDAD RECIENTE", style = MaterialTheme.typography.titleSmall, color = TextGray, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                activities.forEachIndexed { index, activity ->
                    ActivityItem(activity.title, activity.subtitle, activity.time)
                    if (index < activities.size - 1) {
                        HorizontalDivider(color = Color(0xFFF5F5F5), modifier = Modifier.padding(vertical = 12.dp))
                    }
                }
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
