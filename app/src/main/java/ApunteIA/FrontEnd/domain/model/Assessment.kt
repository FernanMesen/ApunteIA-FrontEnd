package ApunteIA.FrontEnd.domain.model


data class Assessment(
    val id: Int,
    val title: String,
    val description: String = "",
    val className: String = "",
    val questions: List<Question> = emptyList(),
    val totalQuestions: Int = questions.size,
    val durationMinutes: Int = 0,
    val isCompleted: Boolean = false
)
