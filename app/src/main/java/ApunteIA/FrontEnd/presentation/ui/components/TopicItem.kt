package ApunteIA.FrontEnd.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.domain.model.AuditSummary
import ApunteIA.FrontEnd.domain.model.Topic
import ApunteIA.FrontEnd.domain.model.TopicStatus
import ApunteIA.FrontEnd.presentation.ui.theme.DarkGreen
import ApunteIA.FrontEnd.presentation.ui.theme.SpaceCadet
import ApunteIA.FrontEnd.presentation.ui.theme.TextGray

private val SolidBarColor = Color(0xFF1B6E63)
private val WeakBarColor = Color(0xFFD08D74)
private val MissingBarColor = Color(0xFFB33A3A)

private fun TopicStatus.barColor(isDark: Boolean): Color = when (this) {
    TopicStatus.SOLID -> if (isDark) Color(0xFF5D7B92) else SolidBarColor
    TopicStatus.WEAK -> if (isDark) Color(0xFFD08D74) else WeakBarColor
    TopicStatus.MISSING -> if (isDark) Color(0xFFC07B7B) else MissingBarColor
}

private fun TopicStatus.badgeColors(isDark: Boolean): Pair<Color, Color> = when (this) {
    TopicStatus.SOLID -> (if (isDark) Color(0xFF2E3B3B) else Color(0xFFE0F2F1)) to (if (isDark) Color(0xFF9BA4B5) else Color(0xFF4DB6AC))
    TopicStatus.WEAK -> (if (isDark) Color(0xFF3B2E2E) else Color(0xFFFDECEC)) to (if (isDark) Color(0xFFD08D74) else Color(0xFFE57373))
    TopicStatus.MISSING -> (if (isDark) Color(0xFF3B2525) else Color(0xFFFCE4E4)) to (if (isDark) Color(0xFFC07B7B) else Color(0xFFD32F2F))
}

@Composable
fun TopicStatusBadge(status: TopicStatus) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    val (backgroundColor, textColor) = status.badgeColors(isDark)
    Box(
        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = status.label, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ProgressTrack(progress: Int, color: Color, modifier: Modifier = Modifier) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    val trackBg = if (isDark) Color(0xFF252B3B) else Color(0xFFEDEDE7)
    
    Box(
        modifier = modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(trackBg)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(fraction = (progress.coerceIn(0, 100) / 100f))
                .height(8.dp).clip(RoundedCornerShape(4.dp)).background(color)
        )
    }
}

@Composable
private fun LegendItem(color: Color, label: String, count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "$label · $count", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun AuditSummaryCard(summary: AuditSummary) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    val trackBg = if (isDark) Color(0xFF252B3B) else Color(0xFFEDEDE7)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = "${summary.percentCovered}%", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "del programa cubierto", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 6.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))

            val hasTopics = summary.solidCount + summary.weakCount + summary.missingCount > 0
            Row(
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(trackBg)
            ) {
                if (hasTopics) {
                    if (summary.solidCount > 0) Box(modifier = Modifier.weight(summary.solidCount.toFloat()).fillMaxWidth().background(TopicStatus.SOLID.barColor(isDark)))
                    if (summary.weakCount > 0) Box(modifier = Modifier.weight(summary.weakCount.toFloat()).fillMaxWidth().background(TopicStatus.WEAK.barColor(isDark)))
                    if (summary.missingCount > 0) Box(modifier = Modifier.weight(summary.missingCount.toFloat()).fillMaxWidth().background(TopicStatus.MISSING.barColor(isDark)))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LegendItem(TopicStatus.SOLID.barColor(isDark), "Sólido", summary.solidCount)
                LegendItem(TopicStatus.WEAK.barColor(isDark), "Débil", summary.weakCount)
                LegendItem(TopicStatus.MISSING.barColor(isDark), "Faltante", summary.missingCount)
            }
        }
    }
}

@Composable
fun TopicCard(topic: Topic, onPracticeClick: () -> Unit) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = topic.title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )
                TopicStatusBadge(status = topic.status)
            }
            Spacer(modifier = Modifier.height(12.dp))
            ProgressTrack(progress = topic.progress, color = topic.status.barColor(isDark))

            if (topic.status != TopicStatus.SOLID) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    modifier = Modifier.clickable { onPracticeClick() },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isDark) Color(0xFF252B3B) else Color(0xFFEBEBE4),
                    border = if (isDark) BorderStroke(1.dp, Color(0xFF394867)) else null
                ) {
                    Text(
                        text = "Practicar esto →",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        color = if (isDark) Color.White else DarkGreen, fontSize = 13.sp, fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}