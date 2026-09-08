package ApunteIA.FrontEnd.domain.repository

import ApunteIA.FrontEnd.domain.model.Topic
import ApunteIA.FrontEnd.domain.util.Resource

interface TopicRepository {
    suspend fun getTopics(): Resource<List<Topic>>
    suspend fun addTopic(className: String): Resource<Topic>
}