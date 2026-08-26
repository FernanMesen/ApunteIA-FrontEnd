package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.TopicDataSource
import ApunteIA.FrontEnd.data.mapper.toDomain
import ApunteIA.FrontEnd.domain.model.Topic
import ApunteIA.FrontEnd.domain.repository.TopicRepository

class TopicRepositoryImpl(
    private val dataSource: TopicDataSource
) : TopicRepository {
    override suspend fun getTopics(): List<Topic> = dataSource.getTopics().toDomain()
}

