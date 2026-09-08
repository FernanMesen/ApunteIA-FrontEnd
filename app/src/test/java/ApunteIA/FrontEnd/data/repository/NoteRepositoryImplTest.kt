package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.NoteDataSource
import ApunteIA.FrontEnd.domain.util.Resource
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class NoteRepositoryImplTest {

    private lateinit var dataSource: NoteDataSource
    private lateinit var repository: NoteRepositoryImpl

    @Before
    fun setup() {
        dataSource = mock()
        repository = NoteRepositoryImpl(dataSource)
    }

    @Test
    fun `getNotes returns Resource Error on IOException`() = runBlocking {
        whenever(dataSource.getNotes()).thenAnswer { throw IOException() }

        val result = repository.getNotes()

        assertTrue(result is Resource.Error)
    }

    @Test
    fun `getNotes returns Resource Error on 500 HttpException`() = runBlocking {
        val errorResponse = Response.error<Any>(500, "".toResponseBody(null))
        whenever(dataSource.getNotes()).thenAnswer { throw HttpException(errorResponse) }

        val result = repository.getNotes()

        assertTrue(result is Resource.Error)
    }
}