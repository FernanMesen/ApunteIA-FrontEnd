package ApunteIA.FrontEnd.data.mapper

import ApunteIA.FrontEnd.domain.error.*
import retrofit2.HttpException
import java.io.IOException

fun Throwable.toNoteError(): NoteError = when (this) {
    is IOException -> NoteError.NoConnection
    is HttpException -> when (code()) {
        in 400..499 -> NoteError.ClientError(code())
        else -> NoteError.ServerError(code())
    }
    else -> NoteError.Unknown
}

fun Throwable.toAuthError(): AuthError = when (this) {
    is IOException -> AuthError.NoConnection
    is HttpException -> when (code()) {
        401, 403 -> AuthError.InvalidCredentials
        else -> AuthError.ServerError(code())
    }
    else -> AuthError.Unknown
}

fun Throwable.toTopicError(): TopicError = when (this) {
    is IOException -> TopicError.Unknown
    else -> TopicError.DataUnavailable
}

fun Throwable.toMainError(): MainError = when (this) {
    is IOException -> MainError.NoConnection
    is HttpException -> MainError.ServerError(code())
    else -> MainError.Unknown
}

fun Throwable.toQuizError(): QuizError = when (this) {
    is IOException -> QuizError.NoConnection
    is HttpException -> QuizError.ServerError(code())
    else -> QuizError.Unknown
}
