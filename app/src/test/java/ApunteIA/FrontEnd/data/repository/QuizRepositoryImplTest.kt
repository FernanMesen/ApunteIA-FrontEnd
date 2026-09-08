package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.QuizDataSource
import ApunteIA.FrontEnd.domain.model.Question
import ApunteIA.FrontEnd.domain.util.Resource
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class QuizRepositoryImplTest {

    private lateinit var dataSource: QuizDataSource
    private lateinit var repository: QuizRepositoryImpl

    @Before
    fun setup() {
        dataSource = mock()
        repository = QuizRepositoryImpl(dataSource)
    }

    @Test
    fun `getQuestions returns Resource Success with data`() = runBlocking {
        whenever(dataSource.getQuestions()).thenReturn(
            listOf(Question(1, "¿Pregunta?", listOf("A", "B"), 0, "Explicación"))
        )

        val result = repository.getQuestions()

        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data.size)
    }

    @Test
    fun `getQuestions returns Resource Error on IOException`() = runBlocking {
        whenever(dataSource.getQuestions()).thenAnswer { throw IOException() }

        val result = repository.getQuestions()

        assertTrue(result is Resource.Error)
    }

    @Test
    fun `getQuestions returns Resource Error on HttpException`() = runBlocking {
        val errorResponse = Response.error<Any>(503, "".toResponseBody(null))
        whenever(dataSource.getQuestions()).thenAnswer { throw HttpException(errorResponse) }

        val result = repository.getQuestions()

        assertTrue(result is Resource.Error)
    }
}