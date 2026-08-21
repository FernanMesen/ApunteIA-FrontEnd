package ApunteIA.FrontEnd.presentation.ui.components

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
import ApunteIA.FrontEnd.presentation.ui.theme.TextGray

private val SolidBarColor = Color(0xFF1B6E63)
private val WeakBarColor = Color(0xFFD08D74)
private val MissingBarColor = Color(0xFFB33A3A)
private val TrackBackgroundColor = Color(0xFFEDEDE7)

private fun TopicStatus.barColor(): Color = when (this) {
    TopicStatus.SOLID -> SolidBarColor
    TopicStatus.WEAK -> WeakBarColor
    TopicStatus.MISSING -> MissingBarColor
}

private fun TopicStatus.badgeColors(): Pair<Color, Color> = when (this) {
    TopicStatus.SOLID -> Color(0xFFE0F2F1) to Color(0xFF4DB6AC)
    TopicStatus.WEAK -> Color(0xFFFDECEC) to Color(0xFFE57373)
    TopicStatus.MISSING -> Color(0xFFFCE4E4) to Color(0xFFD32F2F)
}

@Composable
fun TopicStatusBadge(status: TopicStatus) {
    val (backgroundColor, textColor) = status.badgeColors()
    Box(
        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = status.label, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ProgressTrack(progress: Int, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(TrackBackgroundColor)
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
        Text(text = "$label · $count", fontSize = 13.sp, color = TextGray)
    }
}

@Composable
fun AuditSummaryCard(summary: AuditSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = "${summary.percentCovered}%", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "del programa cubierto", fontSize = 14.sp, color = TextGray, modifier = Modifier.padding(bottom = 6.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))

            val hasTopics = summary.solidCount + summary.weakCount + summary.missingCount > 0
            Row(
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(TrackBackgroundColor)
            ) {
                if (hasTopics) {
                    if (summary.solidCount > 0) Box(modifier = Modifier.weight(summary.solidCount.toFloat()).fillMaxWidth().background(SolidBarColor))
                    if (summary.weakCount > 0) Box(modifier = Modifier.weight(summary.weakCount.toFloat()).fillMaxWidth().background(WeakBarColor))
                    if (summary.missingCount > 0) Box(modifier = Modifier.weight(summary.missingCount.toFloat()).fillMaxWidth().background(MissingBarColor))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LegendItem(SolidBarColor, "Sólido", summary.solidCount)
                LegendItem(WeakBarColor, "Débil", summary.weakCount)
                LegendItem(MissingBarColor, "Faltante", summary.missingCount)
            }
        }
    }
}

@Composable
fun TopicCard(topic: Topic, onPracticeClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = topic.title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = DarkGreen,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )
                TopicStatusBadge(status = topic.status)
            }
            Spacer(modifier = Modifier.height(12.dp))
            ProgressTrack(progress = topic.progress, color = topic.status.barColor())

            if (topic.status != TopicStatus.SOLID) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    modifier = Modifier.clickable { onPracticeClick() },
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFEBEBE4)
                ) {
                    Text(
                        text = "Practicar esto →",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        color = DarkGreen, fontSize = 13.sp, fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}