package ApunteIA.FrontEnd.domain.repository

import ApunteIA.FrontEnd.domain.model.RecentActivity
import ApunteIA.FrontEnd.domain.model.StudyOverview
import ApunteIA.FrontEnd.domain.model.UserStats
import ApunteIA.FrontEnd.domain.util.Resource

interface MainRepository {
    suspend fun getUserStats(): Resource<UserStats>
    suspend fun getRecentActivity(): Resource<List<RecentActivity>>
    suspend fun getOverview(): Resource<StudyOverview>
}