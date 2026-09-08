package ApunteIA.FrontEnd.domain.error

sealed class AuthError : Exception() {
    data object InvalidCredentials : AuthError()
    data object NoConnection : AuthError()
    data class ServerError(val code: Int) : AuthError()
    data object Unknown : AuthError()

    fun toUserMessage(): String = when (this) {
        is InvalidCredentials -> "Correo o contraseña incorrectos."
        is NoConnection -> "No hay conexión a internet para iniciar sesión."
        is ServerError -> "El servidor de autenticación falló (Error $code)."
        is Unknown -> "Ocurrió un error inesperado al iniciar sesión."
    }
}
