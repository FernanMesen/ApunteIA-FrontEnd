package ApunteIA.FrontEnd.data.mapper

import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.data.util.NotLoggedInException
import ApunteIA.FrontEnd.data.util.backendMessage
import ApunteIA.FrontEnd.domain.error.*
import ApunteIA.FrontEnd.util.L10n
import retrofit2.HttpException
import java.io.IOException

fun Throwable.toNoteError(): NoteError = when (this) {
    is IOException -> NoteError.NoConnection
    is HttpException -> when (code()) {
        in 400..499 -> NoteError.ClientError(code(), backendMessage())
        else -> NoteError.ServerError(code(), backendMessage())
    }
    is NotLoggedInException -> NoteError.ClientError(401, L10n.getString(R.string.note_login_required))
    else -> NoteError.Unknown
}

fun Throwable.toAuthError(): AuthError = when (this) {
    is IOException -> AuthError.NoConnection
    is HttpException -> when (code()) {
        401 -> AuthError.InvalidCredentials
        else -> {
            val message = backendMessage()
            if (message != null) AuthError.Rejected(message) else AuthError.ServerError(code())
        }
    }
    else -> AuthError.Unknown
}

fun Throwable.toTopicError(): TopicError = when (this) {
    is IOException -> TopicError.NoConnection
    else -> TopicError.DataUnavailable
}

fun Throwable.toMainError(): MainError = when (this) {
    is IOException -> MainError.NoConnection
    is HttpException -> MainError.ServerError(code())
    else -> MainError.Unknown
}

fun Throwable.toQuizError(): QuizError = when (this) {
    is IOException -> QuizError.NoConnection
    is HttpException -> QuizError.ServerError(code(), backendMessage())
    else -> QuizError.Unknown
}
