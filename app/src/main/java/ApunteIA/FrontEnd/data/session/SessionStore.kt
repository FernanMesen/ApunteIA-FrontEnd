package ApunteIA.FrontEnd.data.session

import ApunteIA.FrontEnd.domain.model.AuthSession
import kotlinx.coroutines.flow.SharedFlow

/** Contrato del almacenamiento de sesión (permite usar un fake en las pruebas). */
interface SessionStore {
    /** JWT vigente, o null si no hay sesión o ya venció. */
    val token: String?
    val userId: Long
    val sessionExpired: SharedFlow<Unit>
    fun save(session: AuthSession)
    fun currentSession(): AuthSession?
    fun isLoggedIn(): Boolean
    fun clear()
    fun onUnauthorized()
}