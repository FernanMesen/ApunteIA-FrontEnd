package ApunteIA.FrontEnd.domain.error

sealed class MainError : Exception() {
    data object NoConnection : MainError()
    data class ServerError(val code: Int) : MainError()
    data object Unknown : MainError()
}