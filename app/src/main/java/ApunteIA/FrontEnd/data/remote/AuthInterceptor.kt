package ApunteIA.FrontEnd.data.remote

import ApunteIA.FrontEnd.data.session.SessionStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Agrega `Authorization: Bearer <JWT>` a todas las peticiones excepto login y registro
 * (las únicas rutas públicas del backend). Si el backend responde 401 a una ruta
 * protegida, la sesión ya no sirve (venció, la cuenta se bloqueó o cambió
 * el JWT_SECRET): se limpia y la app vuelve al login.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val path = original.url.encodedPath
        val isPublic = PUBLIC_PATHS.any { path.endsWith(it) }
        val token = if (isPublic) null else sessionManager.token

        val request = if (token != null) {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }

        val response = chain.proceed(request)
        // También cuando el token ya venció localmente (se mandó sin token): hay que volver al login.
        if (response.code == 401 && !isPublic) {
            sessionManager.onUnauthorized()
        }
        return response
    }

    private companion object {
        val PUBLIC_PATHS = listOf("/auth/login", "/auth/register")
    }
}