package ApunteIA.FrontEnd.domain.repository

import ApunteIA.FrontEnd.domain.model.AuthSession
import ApunteIA.FrontEnd.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    /** Inicia sesión contra POST /auth/login y guarda el JWT. */
    suspend fun login(email: String, password: String, selectedRole: String = "Estudiante"): Resource<AuthSession>

    /** Crea la cuenta (POST /auth/register) y luego inicia sesión. */
    suspend fun register(fullName: String, email: String, password: String): Resource<AuthSession>

    /** Sesión guardada en el dispositivo, o null si no hay / ya venció. */
    fun currentSession(): AuthSession?

    fun logout()

    /** Emite cuando el backend rechaza el token (401) y hay que volver al login. */
    val sessionExpired: Flow<Unit>

    /** Despierta el backend (Render duerme tras 15 min sin tráfico). Ignora errores. */
    suspend fun warmUp()

    suspend fun deleteAccount(): Resource<Unit>
}
