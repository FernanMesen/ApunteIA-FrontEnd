package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.model.Question
import ApunteIA.FrontEnd.domain.repository.QuizRepository

class GetQuizUseCase(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(): Result<List<Question>> {
        return repository.getQuestions()
    }
}
