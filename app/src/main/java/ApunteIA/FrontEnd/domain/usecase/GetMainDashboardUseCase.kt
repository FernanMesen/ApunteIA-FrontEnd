package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.model.RecentActivity
import ApunteIA.FrontEnd.domain.model.StudyOverview
import ApunteIA.FrontEnd.domain.model.UserStats
import ApunteIA.FrontEnd.domain.repository.MainRepository
import ApunteIA.FrontEnd.domain.util.Resource
import javax.inject.Inject

class GetMainDashboardUseCase @Inject constructor(
    private val repository: MainRepository
) {
    suspend fun getUserStats(): Resource<UserStats> = repository.getUserStats()
    suspend fun getRecentActivity(): Resource<List<RecentActivity>> = repository.getRecentActivity()
    suspend fun getOverview(): Resource<StudyOverview> = repository.getOverview()
}