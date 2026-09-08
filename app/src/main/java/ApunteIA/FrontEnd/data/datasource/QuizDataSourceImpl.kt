package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.data.remote.ApiService
import ApunteIA.FrontEnd.domain.model.Question
import javax.inject.Inject

class QuizDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : QuizDataSource {
    override suspend fun getQuestions(): List<Question> {
        return apiService.getQuestions()
    }
}
