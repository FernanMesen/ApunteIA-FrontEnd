package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.QuizDataSource
import ApunteIA.FrontEnd.domain.model.Question
import ApunteIA.FrontEnd.domain.repository.QuizRepository

class QuizRepositoryImpl(
    private val dataSource: QuizDataSource
) : QuizRepository {
    override suspend fun getQuestions(): Result<List<Question>> {
        return try {
            val questions = dataSource.getQuestions()
            Result.success(questions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}



