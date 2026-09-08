package ApunteIA.FrontEnd.domain.error

sealed class QuizError : Exception() {
    data object NoConnection : QuizError()
    data class ServerError(val code: Int) : QuizError()
    data object Unknown : QuizError()
}