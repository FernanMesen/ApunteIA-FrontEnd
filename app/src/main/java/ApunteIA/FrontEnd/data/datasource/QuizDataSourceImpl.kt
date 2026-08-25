package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.domain.model.Question

class QuizDataSourceImpl : QuizDataSource {
    override suspend fun getQuestions(): List<Question> {
        return QuizProvider.getQuestions()
    }
}
