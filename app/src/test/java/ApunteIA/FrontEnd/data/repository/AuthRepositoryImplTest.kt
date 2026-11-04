package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.model.LoginResponseDto
import ApunteIA.FrontEnd.domain.model.LoginRole
import ApunteIA.FrontEnd.domain.usecase.LoginUseCase
import ApunteIA.FrontEnd.domain.util.Resource
import ApunteIA.FrontEnd.fakes.FakeAuthDataSource
import ApunteIA.FrontEnd.fakes.FakeSessionStore
import ApunteIA.FrontEnd.fakes.httpError
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class AuthRepositoryImplTest {

    private lateinit var dataSource: FakeAuthDataSource
    private lateinit var session: FakeSessionStore
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setup() {
        dataSource = FakeAuthDataSource()
        session = FakeSessionStore()
        repository = AuthRepositoryImpl(dataSource, session)
    }

    @Test
    fun `login success maps the JWT response and saves the session`() = runBlocking {
        val result = repository.login(" demo@apunteia.com ", "123456")

        assertTrue(result is Resource.Success)
        val saved = session.currentSession()!!
        assertEquals("jwt", saved.token)
        assertEquals("Maya Rodríguez", saved.userName)
        assertEquals(2L, saved.userId)
        assertEquals(7200L, saved.expiresInSeconds)
    }

    @Test
    fun `login with 401 returns invalid credentials and does not save a session`() = runBlocking {
        dataSource.loginResult = { throw httpError(401, "Credenciales inválidas") }

        val result = repository.login("wrong@user.com", "badpass")

        assertTrue(result is Resource.Error)
        assertEquals("Correo o contraseña incorrectos.", (result as Resource.Error).message)
        assertNull(session.currentSession())
    }

    @Test
    fun `login with blocked account shows the backend message`() = runBlocking {
        dataSource.loginResult = { throw httpError(403, "La cuenta está bloqueada. Contacte al administrador") }

        val result = repository.login("bloqueado@apunteia.com", "123456")

        assertEquals("La cuenta está bloqueada. Contacte al administrador", (result as Resource.Error).message)
    }

    @Test
    fun `login returns connection message on IOException`() = runBlocking {
        dataSource.loginResult = { throw IOException() }

        val result = repository.login("demo@apunteia.com", "123456")

        assertTrue((result as Resource.Error).message.startsWith("No hay conexión con el servidor"))
    }

    @Test
    fun `register creates the account and logs in`() = runBlocking {
        val result = repository.register("Nuevo Usuario", "new@apunteia.com", "secreto1")

        assertTrue(result is Resource.Success)
        assertEquals(Triple("Nuevo Usuario", "new@apunteia.com", "secreto1"), dataSource.registered)
        assertEquals("jwt", session.token)
    }

    @Test
    fun `register with duplicated email shows the backend message`() = runBlocking {
        dataSource.registerResult = { throw httpError(409, "Ya existe un usuario registrado con el email new@apunteia.com") }

        val result = repository.register("Nuevo", "new@apunteia.com", "secreto1")

        assertEquals("Ya existe un usuario registrado con el email new@apunteia.com", (result as Resource.Error).message)
        assertNull(session.currentSession())
    }

    @Test
    fun `logout clears the session`() {
        session.save(FakeSessionStore.loggedIn().session!!)
        repository.logout()
        assertNull(repository.currentSession())
    }

    // ---- F3: la pestaña del Login debe coincidir con el rol de la cuenta ----

    private fun adminLoginResponse() = LoginResponseDto(
        token = "jwt-admin", userName = "Admin", expiresIn = 7200, userId = 1,
        email = "admin@apunteia.com", role = "ADMIN"
    )

    @Test
    fun `student account choosing Administrator is rejected and no session is kept`() = runBlocking {
        val result = LoginUseCase(repository)("demo@apunteia.com", "123456", LoginRole.ADMIN)

        assertTrue(result is Resource.Error)
        assertEquals("Esta cuenta no tiene permisos de administrador.", (result as Resource.Error).message)
        assertNull(session.currentSession())
    }

    @Test
    fun `admin account choosing Student is rejected and no session is kept`() = runBlocking {
        dataSource.loginResult = { adminLoginResponse() }

        val result = LoginUseCase(repository)("admin@apunteia.com", "Admin123!", LoginRole.STUDENT)

        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).message.startsWith("Esta es una cuenta de administrador"))
        assertNull(session.currentSession())
    }

    @Test
    fun `matching role logs in and keeps the session`() = runBlocking {
        dataSource.loginResult = { adminLoginResponse() }
        val admin = LoginUseCase(repository)("admin@apunteia.com", "Admin123!", LoginRole.ADMIN)
        assertTrue(admin is Resource.Success)
        assertEquals("ADMIN", session.currentSession()?.role)

        repository.logout()
        dataSource.loginResult = {
            LoginResponseDto(token = "jwt", userName = "Maya Rodríguez", expiresIn = 7200, userId = 2, email = "demo@apunteia.com", role = "STUDENT")
        }
        val student = LoginUseCase(repository)("demo@apunteia.com", "123456", LoginRole.STUDENT)
        assertTrue(student is Resource.Success)
        assertEquals("STUDENT", session.currentSession()?.role)
    }
}