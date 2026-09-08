package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.AuthDataSource
import ApunteIA.FrontEnd.data.datasource.model.LoginResponseDto
import ApunteIA.FrontEnd.domain.error.AuthError
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

class AuthRepositoryImplTest {

    private lateinit var dataSource: AuthDataSource
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setup() {
        dataSource = mock()
        repository = AuthRepositoryImpl(dataSource)
    }

    @Test
    fun `login success maps to AuthSession`() = runBlocking {
        whenever(dataSource.login("demo@apunteia.com", "123456"))
            .thenReturn(LoginResponseDto(token = "abc", userName = "Maya"))
        val session = repository.login("demo@apunteia.com", "123456")
        assertEquals("abc", session.token)
        assertEquals("Maya", session.userName)
    }

    @Test
    fun `login with 401 throws InvalidCredentials`() = runBlocking {
        val errorResponse = Response.error<Any>(401, "".toResponseBody(null))
        whenever(dataSource.login("wrong@user.com", "badpass"))
            .thenAnswer { throw HttpException(errorResponse) }
        assertThrows(AuthError.InvalidCredentials::class.java) {
            runBlocking { repository.login("wrong@user.com", "badpass") }
        }
        Unit
    }

    @Test
    fun `login throws NoConnection on IOException`() = runBlocking {
        whenever(dataSource.login("demo@apunteia.com", "123456"))
            .thenAnswer { throw IOException() }
        assertThrows(AuthError.NoConnection::class.java) {
            runBlocking { repository.login("demo@apunteia.com", "123456") }
        }
        Unit
    }
}