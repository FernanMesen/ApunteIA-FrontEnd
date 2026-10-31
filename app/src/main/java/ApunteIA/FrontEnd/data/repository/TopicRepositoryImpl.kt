package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.mapper.toTopicError
import ApunteIA.FrontEnd.data.util.safeApiCall
import ApunteIA.FrontEnd.domain.model.Topic
import ApunteIA.FrontEnd.domain.repository.TopicRepository
import ApunteIA.FrontEnd.domain.util.Resource
import javax.inject.Inject

/**
 * El backend no tiene "temas": se derivan de las notas, los cursos inscritos y los
 * intentos de quiz del usuario (ver StudyInsights.buildTopics).
 */
class TopicRepositoryImpl @Inject constructor(
    private val loader: StudyDataLoader
) : TopicRepository {
    override suspend fun getTopics(): Resource<List<Topic>> = safeApiCall(
        errorMapper = { it.toTopicError().toUserMessage() }
    ) {
        loader.overview().topics
    }
}