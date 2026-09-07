package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.model.Question
import ApunteIA.FrontEnd.domain.repository.QuizRepository
import javax.inject.Inject

class GetQuizUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(): Result<List<Question>> {
        return repository.getQuestions()
    }
}
