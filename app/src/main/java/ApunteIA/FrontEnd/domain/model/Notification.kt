package ApunteIA.FrontEnd.domain.model

import androidx.compose.ui.graphics.Color

data class Notification(
    val id: Int,
    val title: String,
    val timeAgo: String,
    val type: NotificationType,
    val isRead: Boolean = false
)

enum class NotificationType(val color: Color) {
    STUDY_REMINDER(Color(0xFF0A3326)),
    ALERT(Color(0xFFC07B7B)),
    INFO(Color(0xFF90A4AE)),
    SUCCESS(Color(0xFF90A4AE))
}
