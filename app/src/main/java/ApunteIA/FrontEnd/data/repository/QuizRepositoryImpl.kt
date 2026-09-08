package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.QuizDataSource
import ApunteIA.FrontEnd.domain.error.QuizError
import ApunteIA.FrontEnd.domain.model.Question
import ApunteIA.FrontEnd.domain.repository.QuizRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val dataSource: QuizDataSource
) : QuizRepository {

    override suspend fun getQuestions(): Result<List<Question>> {
        return try {
            Result.success(dataSource.getQuestions())
        } catch (e: IOException) {
            Result.failure(QuizError.NoConnection)
        } catch (e: HttpException) {
            Result.failure(QuizError.ServerError(e.code()))
        } catch (e: Exception) {
            Result.failure(QuizError.Unknown)
        }
    }
}