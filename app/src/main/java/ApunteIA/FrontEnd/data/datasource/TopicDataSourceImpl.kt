package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.data.datasource.model.TopicDto
import ApunteIA.FrontEnd.data.remote.ApiService
import javax.inject.Inject

class TopicDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : TopicDataSource {
    override suspend fun getTopics(): List<TopicDto> = apiService.getTopics()
    override suspend fun addTopic(topic: TopicDto): TopicDto = apiService.createTopic(topic)
}