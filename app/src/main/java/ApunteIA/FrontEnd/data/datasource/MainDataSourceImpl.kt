package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.data.mapper.toDomain
import ApunteIA.FrontEnd.data.remote.ApiService
import ApunteIA.FrontEnd.domain.model.RecentActivity
import ApunteIA.FrontEnd.domain.model.UserStats
import javax.inject.Inject

class MainDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : MainDataSource {
    override suspend fun getUserStats(): UserStats = apiService.getUserStats().toDomain()
    override suspend fun getRecentActivity(): List<RecentActivity> =
        apiService.getRecentActivity().toDomain()
}