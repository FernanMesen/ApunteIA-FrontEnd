package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.TopicDataSource
import ApunteIA.FrontEnd.data.mapper.toDomain
import ApunteIA.FrontEnd.domain.model.Topic
import ApunteIA.FrontEnd.domain.repository.TopicRepository
import ApunteIA.FrontEnd.domain.util.Resource
import javax.inject.Inject

class TopicRepositoryImpl @Inject constructor(
    private val dataSource: TopicDataSource
) : TopicRepository {
    override suspend fun getTopics(): Resource<List<Topic>> = try {
        Resource.Success(dataSource.getTopics().toDomain())
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Error desconocido", e)
    }
}

