package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.data.datasource.model.TopicDto

class TopicDataSourceImpl : TopicDataSource {
    override suspend fun getTopics(): List<TopicDto> = TopicProvider.topics
}