package ApunteIA.FrontEnd.data.mapper

import ApunteIA.FrontEnd.data.datasource.model.QuestionDto
import ApunteIA.FrontEnd.domain.model.Question

fun QuestionDto.toDomain(): Question = Question(
    id = id,
    text = text,
    options = options,
    correctAnswerIndex = correctAnswerIndex,
    explanation = explanation
)

fun List<QuestionDto>.toDomain(): List<Question> = map { it.toDomain() }