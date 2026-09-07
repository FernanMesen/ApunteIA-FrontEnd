package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.domain.model.Question
import javax.inject.Inject

class QuizDataSourceImpl @Inject constructor() : QuizDataSource {
    override suspend fun getQuestions(): List<Question> {
        return QuizProvider.getQuestions()
    }
}
