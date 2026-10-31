package ApunteIA.FrontEnd.domain.model

/** Dificultad de un quiz generado con IA (mismo enum que el backend). */
enum class QuizDifficulty(val label: String) {
    EASY("Fácil"),
    MEDIUM("Media"),
    HARD("Difícil")
}

/**
 * Un quiz listo para responder: la evaluación del backend + el intento abierto
 * para el usuario actual. Las preguntas llegan sin la respuesta correcta
 * ([Question.correctAnswerIndex] = -1) porque el backend no la expone hasta entregar.
 */
data class QuizSession(
    val assessmentId: Long,
    val attemptId: Long,
    val title: String,
    val questions: List<Question>
)

/** Resultado calificado por el backend al entregar el intento. */
data class QuizResult(
    val scorePercent: Int,
    val answers: List<GradedAnswer>
)

data class GradedAnswer(
    val questionIndex: Int,
    val selectedOptionIndex: Int?,
    val isCorrect: Boolean,
    /** null si el backend desplegado todavía no envía la opción correcta. */
    val correctOptionIndex: Int?
)