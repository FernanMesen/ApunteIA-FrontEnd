package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.error.TopicError
import ApunteIA.FrontEnd.domain.model.Topic
import ApunteIA.FrontEnd.domain.repository.TopicRepository
import javax.inject.Inject

class GetTopicsUseCase @Inject constructor(
    private val repository: TopicRepository
) {
    suspend operator fun invoke(): Result<List<Topic>> {
        return try {
            Result.success(repository.getTopics())
        } catch (e: Exception) {
            Result.failure(TopicError.Unknown)
        }
    }
}