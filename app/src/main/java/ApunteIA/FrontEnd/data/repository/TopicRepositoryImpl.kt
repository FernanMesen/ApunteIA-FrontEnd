package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.TopicDataSource
import ApunteIA.FrontEnd.data.mapper.toDomain
import ApunteIA.FrontEnd.data.mapper.toTopicError
import ApunteIA.FrontEnd.data.util.safeApiCall
import ApunteIA.FrontEnd.domain.model.Topic
import ApunteIA.FrontEnd.domain.model.TopicStatus
import ApunteIA.FrontEnd.domain.repository.TopicRepository
import ApunteIA.FrontEnd.domain.util.Resource
import javax.inject.Inject

class TopicRepositoryImpl @Inject constructor(
    private val dataSource: TopicDataSource
) : TopicRepository {
    override suspend fun getTopics(): Resource<List<Topic>> = safeApiCall(
        errorMapper = { it.toTopicError().toUserMessage() }
    ) {
        dataSource.getTopics().toDomain()
    }

    override suspend fun addTopic(className: String): Resource<Topic> {
        // Simulación de creación para mantener consistencia con la interfaz del repositorio
        return Resource.Success(
            Topic(
                id = (0..1000).random(),
                title = className,
                className = className,
                status = TopicStatus.WEAK,
                progress = 0
            )
        )
    }
}
