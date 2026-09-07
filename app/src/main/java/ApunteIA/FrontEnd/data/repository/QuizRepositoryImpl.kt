package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.QuizDataSource
import ApunteIA.FrontEnd.domain.model.Question
import ApunteIA.FrontEnd.domain.repository.QuizRepository
import ApunteIA.FrontEnd.domain.util.Resource
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val dataSource: QuizDataSource
) : QuizRepository {
    override suspend fun getQuestions(): Resource<List<Question>> {
        return try {
            val questions = dataSource.getQuestions()
            Resource.Success(questions)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error desconocido", e)
        }
    }
}



