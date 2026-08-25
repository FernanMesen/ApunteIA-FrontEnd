package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.domain.model.Question

interface QuizDataSource {
    suspend fun getQuestions(): List<Question>
}
