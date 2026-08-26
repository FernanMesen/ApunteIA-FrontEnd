package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.MainDataSource
import ApunteIA.FrontEnd.domain.model.RecentActivity
import ApunteIA.FrontEnd.domain.model.UserStats
import ApunteIA.FrontEnd.domain.repository.MainRepository

// esta clase Delegan al datasource y aplican los mappers.

class MainRepositoryImpl(
    private val dataSource: MainDataSource
) : MainRepository {
    override suspend fun getUserStats(): Result<UserStats> = try {
        Result.success(dataSource.getUserStats())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getRecentActivity(): Result<List<RecentActivity>> = try {
        Result.success(dataSource.getRecentActivity())
    } catch (e: Exception) {
        Result.failure(e)
    }
}
