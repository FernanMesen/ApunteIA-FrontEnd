package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.domain.model.RecentActivity
import ApunteIA.FrontEnd.domain.util.FormatUtils
import ApunteIA.FrontEnd.presentation.ui.theme.*
import ApunteIA.FrontEnd.presentation.ui.layout.MainLayout
import ApunteIA.FrontEnd.presentation.ui.components.LocalCurrentUser
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
    onCursos: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val isAdmin = LocalCurrentUser.current?.isAdmin == true
    val snackbarHostState = remember { SnackbarHostState() }
    val retryText = stringResource(R.string.retry)

    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMessage ->
            val result = snackbarHostState.showSnackbar(
                message = errorMessage,
                actionLabel = retryText,
                duration = SnackbarDuration.Indefinite
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.refresh()
            }
        }
    }

    MainLayout(
        currentRoute = "Inicio",
        onInicio = onInicio,
        onNotas = onNotas,
        onAuditoria = onAuditoria,
        onReforzar = onReforzar,
        onCursos = onCursos,
        onCameraClick = onCameraClick,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopHeader(
                onProfileClick = onProfileClick
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    uiState.userStats?.let {
                        GreetingSection(it.userName, it.streakDays, isAdmin = isAdmin)
                    }
                }

                if (uiState.isLoading && uiState.userStats == null) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                if (uiState.error != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = uiState.error.orEmpty(),
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.refresh() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text(stringResource(R.string.retry))
                                }
                            }
                        }
                    }
                }

                if (isAdmin) {
                    item {
                        AdminMainSection(onAdminCourses = onCursos)
                    }
                } else {
                    item {
                        uiState.userStats?.let { stats ->
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
                        RecentActivitySection(uiState.recentActivity)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun GreetingSection(userName: String, streakDays: Int, isAdmin: Boolean = false) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = greetingForNow(),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = userName.trim().substringBefore(' ').ifBlank { userName },
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Surface(
                color = if (isDark) Color(0xFF252B3B) else Color.White,
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color.Transparent else Color(0xFFEEEEEE))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0xFF9BA4B5) else DarkGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAdmin) stringResource(R.string.role_admin) else stringResource(R.string.streak_days, streakDays),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color.White else TextGray
                    )
                }
            }
        }
    }
}

@Composable
fun AdminMainSection(onAdminCourses: () -> Unit) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.admin_courses_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.admin_courses_subtitle),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onAdminCourses,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) Color(0xFF394867) else DarkGreen,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.admin_courses_entry),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StatsCard(progress: Int, notesCount: Int, pendingTopics: Int, quizzesDone: Int) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    val formattedProgress = FormatUtils.formatNumber(progress, maxFractionDigits = 0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = if (isDark) Color(0xFF394867) else Color(0xFFEEEEEE),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = if (isDark) Color.White else DarkGreen,
                        startAngle = -90f,
                        sweepAngle = (progress / 100f) * 360f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$formattedProgress%",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (isDark) Color.White else DarkGreen
                    )
                    Text(
                        text = stringResource(R.string.covered_label),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.width(24.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StatLine(
                    label = stringResource(R.string.notes_captured_stat),
                    value = FormatUtils.formatNumber(notesCount, maxFractionDigits = 0),
                    valueColor = if (isDark) Color.White else DarkGreen
                )
                StatLine(
                    label = stringResource(R.string.pending_topics_stat),
                    value = FormatUtils.formatNumber(pendingTopics, maxFractionDigits = 0),
                    valueColor = StatusRed
                )
                StatLine(
                    label = stringResource(R.string.quizzes_done_stat),
                    value = FormatUtils.formatNumber(quizzesDone, maxFractionDigits = 0),
                    valueColor = if (isDark) Color.White else DarkGreen
                )
            }
        }
    }
}

@Composable
fun StatLine(label: String, value: String, valueColor: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable
fun CycleSection(onOrganizarClick: () -> Unit, onReforzarClick: () -> Unit) {
    Column {
        Text(
            text = stringResource(R.string.your_cycle_header),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CycleItem("1", stringResource(R.string.cycle_step_1), false)
            CycleConnector(Modifier.weight(1f))
            CycleItem("2", stringResource(R.string.cycle_step_2), false, onClick = onOrganizarClick)
            CycleConnector(Modifier.weight(1f))
            CycleItem("3", stringResource(R.string.cycle_step_3), false)
            CycleConnector(Modifier.weight(1f))
            CycleItem("4", stringResource(R.string.cycle_step_4), true, onClick = onReforzarClick)
        }
    }
}

@Composable
fun CycleItem(number: String, label: String, isHighlighted: Boolean, onClick: () -> Unit = {}) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet

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
                .background(if (isHighlighted) (if (isDark) Color(0xFF394867) else DarkGreen) else (if (isDark) Color(0xFF252B3B) else Color(0xFFE0E0E0))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontWeight = FontWeight.Bold,
                color = if (isHighlighted) Color.White else (if (isDark) Color(0xFF9BA4B5) else DarkGreen),
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (isHighlighted) (if (isDark) Color.White else DarkGreen) else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
    }
}

@Composable
fun CycleConnector(modifier: Modifier = Modifier) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    Box(
        modifier = modifier
            .height(1.dp)
            .background(if (isDark) Color(0xFF252B3B) else Color(0xFFE0E0E0))
            .offset(y = (-8).dp)
    )
}

@Composable
fun RecentActivitySection(activities: List<RecentActivity>) {
    Column {
        Text(
            text = stringResource(R.string.recent_activity_header),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                activities.forEachIndexed { index, activity ->
                    ActivityItem(activity.title, activity.subtitle, activity.time)
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
fun ActivityItem(title: String, subtitle: String, time: String) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF394867) else Color(0xFFE0E0E0))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Text(
            text = time,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun greetingForNow(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> stringResource(R.string.greeting_morning)
        in 12..18 -> stringResource(R.string.greeting_afternoon)
        else -> stringResource(R.string.greeting_night)
    }
}
