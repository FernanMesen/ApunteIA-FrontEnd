package ApunteIA.FrontEnd.domain.repository

import ApunteIA.FrontEnd.domain.model.Topic

interface TopicRepository {
    suspend fun getTopics(): List<Topic>
}