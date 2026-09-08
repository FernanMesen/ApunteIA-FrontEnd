package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.MainDataSource
import ApunteIA.FrontEnd.data.mapper.toMainError
import ApunteIA.FrontEnd.data.util.safeApiCall
import ApunteIA.FrontEnd.domain.model.RecentActivity
import ApunteIA.FrontEnd.domain.model.UserStats
import ApunteIA.FrontEnd.domain.repository.MainRepository
import ApunteIA.FrontEnd.domain.util.Resource
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val dataSource: MainDataSource
) : MainRepository {

    override suspend fun getUserStats(): Resource<UserStats> = safeApiCall(
        errorMapper = { it.toMainError().toUserMessage() }
    ) {
        dataSource.getUserStats()
    }

    override suspend fun getRecentActivity(): Resource<List<RecentActivity>> = safeApiCall(
        errorMapper = { it.toMainError().toUserMessage() }
    ) {
        dataSource.getRecentActivity()
    }
}
