package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.model.Topic
import ApunteIA.FrontEnd.domain.repository.TopicRepository
import ApunteIA.FrontEnd.domain.util.Resource
import javax.inject.Inject

class GetTopicsUseCase @Inject constructor(
    private val repository: TopicRepository
) {
    suspend operator fun invoke(): Resource<List<Topic>> {
        return repository.getTopics()
    }
}