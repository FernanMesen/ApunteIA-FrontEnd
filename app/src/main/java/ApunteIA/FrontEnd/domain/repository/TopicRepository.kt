package ApunteIA.FrontEnd.domain.repository

import ApunteIA.FrontEnd.domain.model.Topic
import ApunteIA.FrontEnd.domain.util.Resource

interface TopicRepository {
    /**
     * Temas del usuario para la auditoría de brechas: uno por nota (con su
     * dominio según los quizzes resueltos) y uno por cada curso sin apuntes.
     */
    suspend fun getTopics(): Resource<List<Topic>>
}