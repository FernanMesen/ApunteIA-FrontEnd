package ApunteIA.FrontEnd.domain.error

sealed class AuthError : Exception() {
    data object InvalidCredentials : AuthError()
    data object NoConnection : AuthError()
    data class ServerError(val code: Int) : AuthError()
    data object Unknown : AuthError()
}