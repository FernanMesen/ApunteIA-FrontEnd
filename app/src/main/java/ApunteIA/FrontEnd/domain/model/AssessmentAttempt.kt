package ApunteIA.FrontEnd.domain.model

/**
 * Representa un intento realizado por el usuario para resolver una evaluación.
 */
data class AssessmentAttempt(
    val id: Int,
    val assessmentId: String,
    val assessmentTitle: String = "",
    val scorePercent: Int = 0,
    val userAnswers: List<UserAnswer> = emptyList(),
    val completedAt: String = "",
    val durationSeconds: Long = 0L
)
