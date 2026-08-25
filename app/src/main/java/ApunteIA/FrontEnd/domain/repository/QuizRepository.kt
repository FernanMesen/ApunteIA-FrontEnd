package ApunteIA.FrontEnd.domain.repository

import ApunteIA.FrontEnd.domain.model.Question

interface QuizRepository {
    suspend fun getQuestions(): Result<List<Question>>
}
