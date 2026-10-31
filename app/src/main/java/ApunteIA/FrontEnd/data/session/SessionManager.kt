package ApunteIA.FrontEnd.data.session

import android.content.Context
import android.content.SharedPreferences
import ApunteIA.FrontEnd.domain.model.AuthSession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Guarda la sesión del usuario (JWT + datos básicos) en SharedPreferences privadas
 * de la app, para que el login sobreviva a cerrar y abrir la app.
 *
 * - [AuthInterceptor][ApunteIA.FrontEnd.data.remote.AuthInterceptor] lee el token
 *   para mandar `Authorization: Bearer <token>` en cada petición.
 * - Si el backend responde 401 con un token, se limpia la sesión y se emite
 *   [sessionExpired] para que la navegación regrese al login.
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) : SessionStore {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _sessionExpired = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val sessionExpired: SharedFlow<Unit> = _sessionExpired.asSharedFlow()

    override val token: String?
        get() = prefs.getString(KEY_TOKEN, null)?.takeIf { !isExpired() }

    override val userId: Long
        get() = prefs.getLong(KEY_USER_ID, 0L)

    override fun save(session: AuthSession) {
        val expiresAt = if (session.expiresInSeconds > 0) {
            System.currentTimeMillis() + session.expiresInSeconds * 1000
        } else {
            0L
        }
        prefs.edit()
            .putString(KEY_TOKEN, session.token)
            .putString(KEY_USER_NAME, session.userName)
            .putLong(KEY_USER_ID, session.userId)
            .putString(KEY_EMAIL, session.email)
            .putString(KEY_ROLE, session.role)
            .putLong(KEY_EXPIRES_AT, expiresAt)
            .apply()
    }

    override fun currentSession(): AuthSession? {
        val token = token ?: return null
        val userId = userId
        if (userId <= 0L) return null
        return AuthSession(
            token = token,
            userName = prefs.getString(KEY_USER_NAME, "").orEmpty(),
            userId = userId,
            email = prefs.getString(KEY_EMAIL, "").orEmpty(),
            role = prefs.getString(KEY_ROLE, "").orEmpty()
        )
    }

    override fun isLoggedIn(): Boolean = currentSession() != null

    override fun clear() {
        prefs.edit().clear().apply()
    }

    /** Llamado por el interceptor cuando el backend rechaza el token (401). */
    override fun onUnauthorized() {
        if (prefs.contains(KEY_TOKEN)) {
            clear()
            _sessionExpired.tryEmit(Unit)
        }
    }

    private fun isExpired(): Boolean {
        val expiresAt = prefs.getLong(KEY_EXPIRES_AT, 0L)
        // Margen de 30 s para no mandar un token que vence en pleno viaje.
        return expiresAt > 0L && System.currentTimeMillis() > expiresAt - 30_000
    }

    private companion object {
        const val PREFS_NAME = "apunteia_session"
        const val KEY_TOKEN = "token"
        const val KEY_USER_NAME = "user_name"
        const val KEY_USER_ID = "user_id"
        const val KEY_EMAIL = "email"
        const val KEY_ROLE = "role"
        const val KEY_EXPIRES_AT = "expires_at"
    }
}