package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.TopicDataSource
import ApunteIA.FrontEnd.data.datasource.model.TopicDto
import ApunteIA.FrontEnd.data.mapper.toDomain
import ApunteIA.FrontEnd.data.util.safeApiCall
import ApunteIA.FrontEnd.domain.model.Topic
import ApunteIA.FrontEnd.domain.repository.TopicRepository
import ApunteIA.FrontEnd.domain.util.Resource
import javax.inject.Inject

class TopicRepositoryImpl @Inject constructor(
    private val dataSource: TopicDataSource
) : TopicRepository {

    override suspend fun getTopics(): Resource<List<Topic>> = safeApiCall {
        dataSource.getTopics().toDomain()
    }

    override suspend fun addTopic(className: String): Resource<Topic> = safeApiCall {
        val newTopic = TopicDto(
            id = 0,
            title = "Introducción al curso",
            className = className,
            status = "Faltante",
            progress = 0
        )
        dataSource.addTopic(newTopic).toDomain()
    }
}