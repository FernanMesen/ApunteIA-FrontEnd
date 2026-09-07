package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.model.Question
import ApunteIA.FrontEnd.domain.repository.QuizRepository
import ApunteIA.FrontEnd.domain.util.Resource
import javax.inject.Inject

class GetQuizUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(): Resource<List<Question>> {
        return repository.getQuestions()
    }
}
