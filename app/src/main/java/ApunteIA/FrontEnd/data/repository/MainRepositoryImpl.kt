package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.mapper.toMainError
import ApunteIA.FrontEnd.data.util.safeApiCall
import ApunteIA.FrontEnd.domain.model.RecentActivity
import ApunteIA.FrontEnd.domain.model.StudyOverview
import ApunteIA.FrontEnd.domain.model.UserStats
import ApunteIA.FrontEnd.domain.repository.MainRepository
import ApunteIA.FrontEnd.domain.usecase.StudyInsights
import ApunteIA.FrontEnd.domain.util.Resource
import javax.inject.Inject

/** Tablero: estadísticas y actividad calculadas a partir de los datos reales del usuario. */
class MainRepositoryImpl @Inject constructor(
    private val loader: StudyDataLoader
) : MainRepository {

    private val errorMapper: (Throwable) -> String = { it.toMainError().toUserMessage() }

    override suspend fun getUserStats(): Resource<UserStats> = safeApiCall(errorMapper) {
        val overview = loader.overview(maxAgeMillis = CACHE_MILLIS)
        StudyInsights.userStats(overview.userName, overview.notes, overview.topics, overview.attempts)
    }

    override suspend fun getRecentActivity(): Resource<List<RecentActivity>> = safeApiCall(errorMapper) {
        val overview = loader.overview(maxAgeMillis = CACHE_MILLIS)
        StudyInsights.recentActivity(overview.notes, overview.attempts)
    }

    override suspend fun getOverview(): Resource<StudyOverview> = safeApiCall(errorMapper) {
        loader.overview(maxAgeMillis = CACHE_MILLIS)
    }

    private companion object {
        const val CACHE_MILLIS = 5_000L
    }
}