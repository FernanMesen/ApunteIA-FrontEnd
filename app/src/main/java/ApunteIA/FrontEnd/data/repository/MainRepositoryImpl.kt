package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.MainDataSource
import ApunteIA.FrontEnd.domain.error.MainError
import ApunteIA.FrontEnd.domain.model.RecentActivity
import ApunteIA.FrontEnd.domain.model.UserStats
import ApunteIA.FrontEnd.domain.repository.MainRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val dataSource: MainDataSource
) : MainRepository {

    override suspend fun getUserStats(): Result<UserStats> = wrapExceptions {
        dataSource.getUserStats()
    }

    override suspend fun getRecentActivity(): Result<List<RecentActivity>> = wrapExceptions {
        dataSource.getRecentActivity()
    }

    private suspend fun <T> wrapExceptions(block: suspend () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (e: IOException) {
            Result.failure(MainError.NoConnection)
        } catch (e: HttpException) {
            Result.failure(MainError.ServerError(e.code()))
        } catch (e: Exception) {
            Result.failure(MainError.Unknown)
        }
    }
}