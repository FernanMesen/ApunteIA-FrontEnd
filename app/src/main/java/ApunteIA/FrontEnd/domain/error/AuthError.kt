package ApunteIA.FrontEnd.domain.error

import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.util.L10n

sealed class AuthError : Exception() {
    data object InvalidCredentials : AuthError()
    data object NoConnection : AuthError()
    data class ServerError(val code: Int) : AuthError()
    /** El backend rechazó la petición con un mensaje propio (cuenta bloqueada, email repetido...). */
    data class Rejected(val reason: String) : AuthError()
    data object RoleMismatchStudent : AuthError()
    data object RoleMismatchAdmin : AuthError()
    data object Unknown : AuthError()

    fun toUserMessage(): String = when (this) {
        is InvalidCredentials -> L10n.getString(R.string.auth_error_credentials)
        is NoConnection -> L10n.getString(R.string.auth_error_connection)
        is ServerError -> L10n.getString(R.string.auth_error_server, code)
        is Rejected -> reason
        is RoleMismatchStudent -> L10n.getString(R.string.login_error_role_mismatch_student)
        is RoleMismatchAdmin -> L10n.getString(R.string.login_error_role_mismatch_admin)
        is Unknown -> L10n.getString(R.string.auth_error_unknown)
    }
}
