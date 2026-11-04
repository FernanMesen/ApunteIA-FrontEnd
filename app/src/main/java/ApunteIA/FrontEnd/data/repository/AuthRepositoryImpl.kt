package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.AuthDataSource
import ApunteIA.FrontEnd.data.mapper.toAuthError
import ApunteIA.FrontEnd.data.mapper.toDomain
import ApunteIA.FrontEnd.data.session.SessionStore
import ApunteIA.FrontEnd.data.util.safeApiCall
import ApunteIA.FrontEnd.domain.error.AuthError
import ApunteIA.FrontEnd.domain.model.AuthSession
import ApunteIA.FrontEnd.domain.repository.AuthRepository
import ApunteIA.FrontEnd.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val dataSource: AuthDataSource,
    private val sessionManager: SessionStore
) : AuthRepository {

    override suspend fun login(email: String, password: String, selectedRole: String): Resource<AuthSession> = safeApiCall(
        errorMapper = { it.toAuthError().toUserMessage() }
    ) {
        val session = dataSource.login(email.trim(), password).toDomain()
        val isSelectedAdmin = selectedRole.equals("Administrador", ignoreCase = true)
        val isBackendStudent = session.role.equals("STUDENT", ignoreCase = true)
        if (isSelectedAdmin && isBackendStudent) {
            throw AuthError.RoleMismatchStudent
        }

        val isSelectedStudent = selectedRole.equals("Estudiante", ignoreCase = true)
        val isBackendAdmin = session.role.equals("ADMIN", ignoreCase = true)
        if (isSelectedStudent && isBackendAdmin) {
            throw AuthError.RoleMismatchAdmin
        }

        sessionManager.save(session)
        session
    }

    override suspend fun register(fullName: String, email: String, password: String): Resource<AuthSession> =
        safeApiCall(errorMapper = { it.toAuthError().toUserMessage() }) {
            dataSource.register(fullName, email, password)
            val session = dataSource.login(email, password).toDomain()
            sessionManager.save(session)
            session
        }

    override fun currentSession(): AuthSession? = sessionManager.currentSession()

    override fun logout() = sessionManager.clear()

    override val sessionExpired: Flow<Unit> get() = sessionManager.sessionExpired

    override suspend fun warmUp() {
        runCatching { dataSource.ping() }
    }
}
