package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.data.datasource.model.TopicDto

interface TopicDataSource {
    suspend fun getTopics(): List<TopicDto>
}