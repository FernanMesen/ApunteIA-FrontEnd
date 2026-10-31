package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.model.Assessment
import ApunteIA.FrontEnd.domain.model.AssessmentAttempt
import ApunteIA.FrontEnd.domain.model.QuizResult
import ApunteIA.FrontEnd.domain.model.QuizSession
import ApunteIA.FrontEnd.domain.repository.QuizRepository
import ApunteIA.FrontEnd.domain.util.Resource
import javax.inject.Inject

class GetAvailableQuizzesUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(): Resource<List<Assessment>> = repository.getAvailableQuizzes()
}

class StartQuizUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(assessmentId: Long): Resource<QuizSession> = repository.startQuiz(assessmentId)
}

class SubmitQuizUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(attemptId: Long, answers: List<Int?>): Resource<QuizResult> =
        repository.submitQuiz(attemptId, answers)
}

class GetMyAttemptsUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(): Resource<List<AssessmentAttempt>> = repository.getMyAttempts()
}