package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.TopicDataSource
import ApunteIA.FrontEnd.data.datasource.model.TopicDto
import ApunteIA.FrontEnd.data.mapper.toDomain
import ApunteIA.FrontEnd.domain.error.TopicError
import ApunteIA.FrontEnd.domain.model.Topic
import ApunteIA.FrontEnd.domain.repository.TopicRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class TopicRepositoryImpl @Inject constructor(
    private val dataSource: TopicDataSource
) : TopicRepository {

    override suspend fun getTopics(): List<Topic> = wrapExceptions {
        dataSource.getTopics().toDomain()
    }

    override suspend fun addTopic(className: String): Topic = wrapExceptions {
        val newTopic = TopicDto(
            id = 0,
            title = "Introducción al curso",
            className = className,
            status = "Faltante",
            progress = 0
        )
        dataSource.addTopic(newTopic).toDomain()
    }

    private suspend fun <T> wrapExceptions(block: suspend () -> T): T {
        return try {
            block()
        } catch (e: IOException) {
            throw TopicError.DataUnavailable
        } catch (e: HttpException) {
            throw TopicError.DataUnavailable
        } catch (e: Exception) {
            throw TopicError.Unknown
        }
    }
}