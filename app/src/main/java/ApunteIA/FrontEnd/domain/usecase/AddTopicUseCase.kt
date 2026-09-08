package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.model.Topic
import ApunteIA.FrontEnd.domain.repository.TopicRepository
import ApunteIA.FrontEnd.domain.util.Resource
import javax.inject.Inject

class AddTopicUseCase @Inject constructor(
    private val repository: TopicRepository
) {
    suspend operator fun invoke(className: String): Resource<Topic> {
        return repository.addTopic(className)
    }
}