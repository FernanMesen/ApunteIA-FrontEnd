package ApunteIA.FrontEnd.domain.error

import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.util.L10n

sealed class QuizError : Exception() {
    data object NoConnection : QuizError()
    data class ServerError(val code: Int, val detail: String? = null) : QuizError()
    data object Unknown : QuizError()

    fun toUserMessage(): String = when (this) {
        is NoConnection -> L10n.getString(R.string.quiz_error_connection)
        is ServerError -> detail ?: L10n.getString(R.string.quiz_error_server, code)
        is Unknown -> L10n.getString(R.string.quiz_error_unknown)
    }
}
