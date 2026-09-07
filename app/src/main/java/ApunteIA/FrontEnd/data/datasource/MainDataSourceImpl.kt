package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.domain.model.RecentActivity
import ApunteIA.FrontEnd.domain.model.UserStats
import javax.inject.Inject

class MainDataSourceImpl @Inject constructor() : MainDataSource {
    override suspend fun getUserStats(): UserStats = MainProvider.getUserStats()
    override suspend fun getRecentActivity(): List<RecentActivity> = MainProvider.getRecentActivity()
}
