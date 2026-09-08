package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.TopicDataSource
import ApunteIA.FrontEnd.data.datasource.model.TopicDto
import ApunteIA.FrontEnd.domain.util.Resource
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
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

        assertTrue(result is Resource.Success)
        val data = (result as Resource.Success).data
        assertEquals(1, data.size)
        assertEquals("Estereoquímica", data[0].title)
    }

    @Test
    fun `getTopics returns Resource Error on IOException`() = runBlocking {
        whenever(dataSource.getTopics()).thenAnswer { throw IOException() }

        val result = repository.getTopics()

        assertTrue(result is Resource.Error)
    }

    @Test
    fun `getTopics returns Resource Error on HttpException`() = runBlocking {
        val errorResponse = Response.error<Any>(500, "".toResponseBody(null))
        whenever(dataSource.getTopics()).thenAnswer { throw HttpException(errorResponse) }

        val result = repository.getTopics()

        assertTrue(result is Resource.Error)
    }

    @Test
    fun `addTopic creates topic with given className`() = runBlocking {
        whenever(dataSource.addTopic(any())).thenReturn(
            TopicDto(5, "Introducción al curso", "Curso Nuevo", "Faltante", 0)
        )

        val result = repository.addTopic("Curso Nuevo")

        assertTrue(result is Resource.Success)
        assertEquals("Curso Nuevo", (result as Resource.Success).data.className)
    }
}