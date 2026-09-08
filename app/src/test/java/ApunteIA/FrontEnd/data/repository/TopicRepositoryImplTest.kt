package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.TopicDataSource
import ApunteIA.FrontEnd.data.datasource.model.TopicDto
import ApunteIA.FrontEnd.domain.error.TopicError
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class TopicRepositoryImplTest {

    private lateinit var dataSource: TopicDataSource
    private lateinit var repository: TopicRepositoryImpl

    @Before
    fun setup() {
        dataSource = mock()
        repository = TopicRepositoryImpl(dataSource)
    }

    @Test
    fun `getTopics maps dtos to domain`() = runBlocking {
        whenever(dataSource.getTopics()).thenReturn(
            listOf(TopicDto(1, "Estereoquímica", "Química Orgánica", "Débil", 50))
        )
        val result = repository.getTopics()
        assertEquals(1, result.size)
        assertEquals("Estereoquímica", result[0].title)
    }

    @Test
    fun `getTopics throws DataUnavailable on IOException`() = runBlocking {
        whenever(dataSource.getTopics()).thenAnswer { throw IOException() }
        assertThrows(TopicError.DataUnavailable::class.java) {
            runBlocking { repository.getTopics() }
        }
        Unit
    }

    @Test
    fun `getTopics throws DataUnavailable on HttpException`() = runBlocking {
        val errorResponse = Response.error<Any>(500, "".toResponseBody(null))
        whenever(dataSource.getTopics()).thenAnswer { throw HttpException(errorResponse) }
        assertThrows(TopicError.DataUnavailable::class.java) {
            runBlocking { repository.getTopics() }
        }
        Unit
    }

    @Test
    fun `addTopic creates topic with given className`() = runBlocking {
        whenever(dataSource.addTopic(org.mockito.kotlin.any())).thenReturn(
            TopicDto(5, "Introducción al curso", "Curso Nuevo", "Faltante", 0)
        )
        val result = repository.addTopic("Curso Nuevo")
        assertEquals("Curso Nuevo", result.className)
    }
}