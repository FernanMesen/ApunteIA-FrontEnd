package ApunteIA.FrontEnd.domain.repository

import ApunteIA.FrontEnd.domain.model.Question
import ApunteIA.FrontEnd.domain.util.Resource

interface QuizRepository {
    suspend fun getQuestions(): Resource<List<Question>>
}
