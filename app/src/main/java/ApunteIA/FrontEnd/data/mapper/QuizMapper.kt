package ApunteIA.FrontEnd.data.mapper

import ApunteIA.FrontEnd.data.datasource.model.AssessmentDto
import ApunteIA.FrontEnd.data.datasource.model.AttemptDto
import ApunteIA.FrontEnd.domain.model.Assessment
import ApunteIA.FrontEnd.domain.model.AssessmentAttempt
import ApunteIA.FrontEnd.domain.model.GradedAnswer
import ApunteIA.FrontEnd.domain.model.Question
import ApunteIA.FrontEnd.domain.model.QuizResult
import ApunteIA.FrontEnd.domain.model.QuizSession
import ApunteIA.FrontEnd.domain.model.UserAnswer
import kotlin.math.roundToInt

/** Preguntas sin respuesta correcta (-1): el backend la oculta hasta entregar el intento. */
fun AssessmentDto.toQuestions(): List<Question> =
    questions.orEmpty()
        .sortedBy { it.index }
        .map { q ->
            Question(
                id = q.index,
                text = q.text.orEmpty(),
                options = q.options.orEmpty(),
                correctAnswerIndex = -1,
                explanation = ""
            )
        }

fun AssessmentDto.toSession(attemptId: Long): QuizSession = QuizSession(
    assessmentId = id,
    attemptId = attemptId,
    title = title.orEmpty(),
    questions = toQuestions()
)

fun AssessmentDto.toDomain(): Assessment = Assessment(
    id = id.toInt(),
    title = title.orEmpty(),
    description = description.orEmpty(),
    className = courseName.orEmpty(),
    questions = toQuestions(),
    totalQuestions = totalQuestions ?: questions.orEmpty().size,
    durationMinutes = durationMinutes ?: 0
)

fun AttemptDto.toDomain(): AssessmentAttempt = AssessmentAttempt(
    id = id.toInt(),
    assessmentId = assessmentId.toString(),
    assessmentTitle = assessmentTitle.orEmpty(),
    scorePercent = (score ?: 0.0).roundToInt(),
    userAnswers = answers.orEmpty().map {
        UserAnswer(
            questionId = it.questionIndex,
            selectedOptionIndex = it.selectedOptionIndex ?: -1,
            isCorrect = it.correct
        )
    },
    completedAt = if (status.equals("COMPLETED", ignoreCase = true)) completedAt.orEmpty() else "",
    durationSeconds = durationSeconds ?: 0L
)

fun AttemptDto.toResult(): QuizResult = QuizResult(
    scorePercent = (score ?: 0.0).roundToInt(),
    answers = answers.orEmpty()
        .sortedBy { it.questionIndex }
        .map {
            GradedAnswer(
                questionIndex = it.questionIndex,
                selectedOptionIndex = it.selectedOptionIndex,
                isCorrect = it.correct,
                correctOptionIndex = it.correctOptionIndex
            )
        }
)