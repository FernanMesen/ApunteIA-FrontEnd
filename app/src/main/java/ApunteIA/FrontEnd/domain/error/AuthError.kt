package ApunteIA.FrontEnd.domain.error

sealed class AuthError : Exception() {
    data object InvalidCredentials : AuthError()
    data object NoConnection : AuthError()
    data class ServerError(val code: Int) : AuthError()
    /** El backend rechazó la petición con un mensaje propio (cuenta bloqueada, email repetido...). */
    data class Rejected(val reason: String) : AuthError()
    data object Unknown : AuthError()

    fun toUserMessage(): String = when (this) {
        is InvalidCredentials -> "Correo o contraseña incorrectos."
        is NoConnection -> "No hay conexión con el servidor. Si es la primera vez en un rato, espera ~1 min: el servidor se está despertando."
        is ServerError -> "El servidor de autenticación falló (Error $code)."
        is Rejected -> reason
        is Unknown -> "Ocurrió un error inesperado al iniciar sesión."
    }
}