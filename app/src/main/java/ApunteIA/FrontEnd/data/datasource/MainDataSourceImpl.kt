package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.domain.model.RecentActivity
import ApunteIA.FrontEnd.domain.model.UserStats

class MainDataSourceImpl : MainDataSource {
    override suspend fun getUserStats(): UserStats = MainProvider.getUserStats()
    override suspend fun getRecentActivity(): List<RecentActivity> = MainProvider.getRecentActivity()
}
