package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.data.datasource.model.TopicDto
import javax.inject.Inject

class TopicDataSourceImpl @Inject constructor() : TopicDataSource {
    override suspend fun getTopics(): List<TopicDto> = TopicProvider.topics
}