package ApunteIA.FrontEnd.domain.model


data class AssessmentAttempt(
    val id: Int,
    val assessmentId: String,
    val assessmentTitle: String = "",
    val scorePercent: Int = 0,
    val userAnswers: List<UserAnswer> = emptyList(),
    val completedAt: String = "",
    val durationSeconds: Long = 0L
)
