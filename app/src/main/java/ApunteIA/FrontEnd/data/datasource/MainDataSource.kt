package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.domain.model.RecentActivity
import ApunteIA.FrontEnd.domain.model.UserStats

interface MainDataSource {
    suspend fun getUserStats(): UserStats
    suspend fun getRecentActivity(): List<RecentActivity>
}
