package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.model.RecentActivity
import ApunteIA.FrontEnd.domain.model.UserStats
import ApunteIA.FrontEnd.domain.repository.MainRepository

class GetMainDashboardUseCase(
    private val repository: MainRepository
) {
    suspend fun getUserStats(): Result<UserStats> = repository.getUserStats()
    suspend fun getRecentActivity(): Result<List<RecentActivity>> = repository.getRecentActivity()
}
