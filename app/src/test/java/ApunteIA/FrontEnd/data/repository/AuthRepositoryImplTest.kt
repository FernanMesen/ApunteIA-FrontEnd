package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.AuthDataSource
import ApunteIA.FrontEnd.data.datasource.model.LoginResponseDto
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
        val result = repository.login("demo@apunteia.com", "123456")
        assertTrue(result is Resource.Success)
        val session = (result as Resource.Success).data
        assertEquals("abc", session.token)
        assertEquals("Maya", session.userName)
    }

    @Test
    fun `login with 401 returns Error resource`() = runBlocking {
        val errorResponse = Response.error<Any>(401, "".toResponseBody(null))
        whenever(dataSource.login("wrong@user.com", "badpass"))
            .thenAnswer { throw HttpException(errorResponse) }
        val result = repository.login("wrong@user.com", "badpass")
        assertTrue(result is Resource.Error)
        assertEquals("Correo o contraseña incorrectos.", (result as Resource.Error).message)
    }

    @Test
    fun `login returns Error resource on IOException`() = runBlocking {
        whenever(dataSource.login("demo@apunteia.com", "123456"))
            .thenAnswer { throw IOException() }
        val result = repository.login("demo@apunteia.com", "123456")
        assertTrue(result is Resource.Error)
        assertEquals("No hay conexión a internet para iniciar sesión.", (result as Resource.Error).message)
    }
}