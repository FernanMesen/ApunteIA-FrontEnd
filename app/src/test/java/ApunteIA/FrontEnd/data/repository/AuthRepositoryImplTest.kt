package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.model.LoginResponseDto
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
    fun `login with admin role selected but student credentials returns mismatch error and does not save session`() = runBlocking {
        dataSource.loginResult = {
            LoginResponseDto(
                token = "jwt",
                userName = "Estudiante",
                userId = 2L,
                email = "demo@apunteia.com",
                role = "STUDENT"
            )
        }

        val result = repository.login("demo@apunteia.com", "123456", selectedRole = "Administrador")

        assertTrue(result is Resource.Error)
        assertEquals("Estas credenciales pertenecen a una cuenta de estudiante. Selecciona Estudiante para continuar.", (result as Resource.Error).message)
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
}
