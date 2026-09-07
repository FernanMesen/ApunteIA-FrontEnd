package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
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
import androidx.hilt.navigation.compose.hiltViewModel
import ApunteIA.FrontEnd.domain.model.RecentActivity
import ApunteIA.FrontEnd.presentation.ui.theme.*
import ApunteIA.FrontEnd.presentation.viewmodel.StudyHistoryViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.StudyStats

@Composable
fun StudyHistoryScreen(
    onBack: () -> Unit,
    viewModel: StudyHistoryViewModel = hiltViewModel()
) {
    val isWeekly by viewModel.isWeekly.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val recentActivity by viewModel.recentActivity.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            StudyHistoryTopBar(onBack = onBack)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                PeriodSelector(isWeekly = isWeekly, onPeriodSelected = { viewModel.setPeriod(it) })
            }

            item {
                StatsOverviewRow(stats = stats)
            }

            item {
                WeeklyChartCard(progress = stats.weeklyProgress)
            }

            item {
                SubjectProgressSection()
            }

            item {
                SummaryCards(stats = stats)
            }

            item {
                RecentActivityHistory(activities = recentActivity)
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun StudyHistoryTopBar(onBack: () -> Unit) {
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
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = "Historial de estudio",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Tu progreso a través del tiempo",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PeriodSelector(isWeekly: Boolean, onPeriodSelected: (Boolean) -> Unit) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    
    Row(
        modifier = Modifier
            .background(if (isDark) Color(0xFF1E2532) else Color(0xFFF5F5F5), RoundedCornerShape(24.dp))
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (isWeekly) (if (isDark) Color(0xFF394867) else DarkGreen) else Color.Transparent)
                .clickable { onPeriodSelected(true) }
                .padding(horizontal = 24.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Semanal",
                color = if (isWeekly) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (!isWeekly) (if (isDark) Color(0xFF394867) else DarkGreen) else Color.Transparent)
                .clickable { onPeriodSelected(false) }
                .padding(horizontal = 24.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Mensual",
                color = if (!isWeekly) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StatsOverviewRow(stats: StudyStats) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatBox(value = "${stats.precision}%", label = "Precisión", modifier = Modifier.weight(1f))
        StatBox(value = "${stats.streak}", label = "Racha (días)", modifier = Modifier.weight(1f))
        StatBox(value = stats.studyTime, label = "Tiempo de estudio", modifier = Modifier.weight(1f))
    }
}

@Composable
fun StatBox(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun WeeklyChartCard(progress: List<Float>) {
    val days = listOf("L", "M", "M", "J", "V", "S", "D")
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                progress.forEachIndexed { index, value ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .fillMaxHeight(value)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(if (isDark) Color(0xFF5D7B92) else DarkGreen)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = days[index], fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectProgressSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "PROGRESO POR MATERIA",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        SubjectProgressItem("Química Orgánica", 54, Color(0xFF1B6E63))
        SubjectProgressItem("Biología Celular", 67, Color(0xFF006064))
        SubjectProgressItem("Cálculo II", 15, Color(0xFFB33A3A))
    }
}

@Composable
fun SubjectProgressItem(name: String, progress: Int, color: Color) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = name, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "$progress%", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(if (isDark) Color(0xFF252B3B) else Color(0xFFF0F0F0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress / 100f)
                    .fillMaxHeight()
                    .background(color.copy(alpha = if (isDark) 0.6f else 1f))
            )
        }
    }
}

@Composable
fun SummaryCards(stats: StudyStats) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SummaryCard(
            label = "Áreas fuertes",
            value = stats.strongAreas.toString(),
            color = Color(0xFF2E3B3B),
            textColor = Color(0xFF5D7B92),
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            label = "Áreas débiles",
            value = stats.weakAreas.toString(),
            color = Color(0xFF3B2525),
            textColor = Color(0xFFC07B7B),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SummaryCard(label: String, value: String, color: Color, textColor: Color, modifier: Modifier = Modifier) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) color else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = if (!isDark) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE)) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, fontSize = 12.sp, color = textColor, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun RecentActivityHistory(activities: List<RecentActivity>) {
    Column {
        Text(
            text = "ACTIVIDAD RECIENTE",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                activities.forEachIndexed { index, activity ->
                    ActivityHistoryItem(activity.title, activity.time)
                    if (index < activities.size - 1) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityHistoryItem(title: String, time: String) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF5D7B92) else DarkGreen)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = time,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
