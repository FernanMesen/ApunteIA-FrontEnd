package ApunteIA.FrontEnd.domain.model

data class UserAnswer(
    val questionId: Int,
    val selectedOptionIndex: Int,
    val isCorrect: Boolean
)
