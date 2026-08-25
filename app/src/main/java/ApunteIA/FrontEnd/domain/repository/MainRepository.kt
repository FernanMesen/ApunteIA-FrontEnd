package ApunteIA.FrontEnd.domain.repository

import ApunteIA.FrontEnd.domain.model.RecentActivity
import ApunteIA.FrontEnd.domain.model.UserStats

interface MainRepository {
    suspend fun getUserStats(): Result<UserStats>
    suspend fun getRecentActivity(): Result<List<RecentActivity>>
}
