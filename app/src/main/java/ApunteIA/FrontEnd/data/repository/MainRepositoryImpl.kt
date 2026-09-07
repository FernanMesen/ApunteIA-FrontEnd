package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.MainDataSource
import ApunteIA.FrontEnd.domain.model.RecentActivity
import ApunteIA.FrontEnd.domain.model.UserStats
import ApunteIA.FrontEnd.domain.repository.MainRepository
import ApunteIA.FrontEnd.domain.util.Resource
import javax.inject.Inject

// esta clase Delegan al datasource y aplican los mappers.

class MainRepositoryImpl @Inject constructor(
    private val dataSource: MainDataSource
) : MainRepository {
    override suspend fun getUserStats(): Resource<UserStats> = try {
        Resource.Success(dataSource.getUserStats())
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Error desconocido", e)
    }

    override suspend fun getRecentActivity(): Resource<List<RecentActivity>> = try {
        Resource.Success(dataSource.getRecentActivity())
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Error desconocido", e)
    }
}
