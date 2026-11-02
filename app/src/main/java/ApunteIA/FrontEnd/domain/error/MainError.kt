package ApunteIA.FrontEnd.domain.error

import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.util.L10n

sealed class MainError : Exception() {
    data object NoConnection : MainError()
    data class ServerError(val code: Int) : MainError()
    data object Unknown : MainError()

    fun toUserMessage(): String = when (this) {
        is NoConnection -> L10n.getString(R.string.main_error_connection)
        is ServerError -> L10n.getString(R.string.main_error_server, code)
        is Unknown -> L10n.getString(R.string.main_error_unknown)
    }
}
