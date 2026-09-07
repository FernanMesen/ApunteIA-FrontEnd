package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.NoteDataSource
import ApunteIA.FrontEnd.domain.error.NoteError
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertThrows
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
    fun `getNotes throws NoConnection on IOException`() = runBlocking {
        whenever(dataSource.getNotes()).thenAnswer { throw IOException() }

        assertThrows(NoteError.NoConnection::class.java) {
            runBlocking { repository.getNotes() }
        }
        Unit
    }

    @Test
    fun `getNotes throws ServerError on 500 HttpException`() = runBlocking {
        val errorResponse = Response.error<Any>(500, "".toResponseBody(null))
        whenever(dataSource.getNotes()).thenAnswer { throw HttpException(errorResponse) }

        assertThrows(NoteError.ServerError::class.java) {
            runBlocking { repository.getNotes() }
        }
        Unit
    }
}
