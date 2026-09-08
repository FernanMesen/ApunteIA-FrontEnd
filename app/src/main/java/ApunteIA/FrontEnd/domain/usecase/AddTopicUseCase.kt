package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.error.TopicError
import ApunteIA.FrontEnd.domain.model.Topic
import ApunteIA.FrontEnd.domain.repository.TopicRepository
import javax.inject.Inject

class AddTopicUseCase @Inject constructor(
    private val repository: TopicRepository
) {
    suspend operator fun invoke(className: String): Result<Topic> {
        return try {
            Result.success(repository.addTopic(className))
        } catch (e: Exception) {
            Result.failure(e as? TopicError ?: TopicError.Unknown)
        }
    }
}