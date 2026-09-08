package ApunteIA.FrontEnd.domain.error

sealed class MainError : Exception() {
    data object NoConnection : MainError()
    data class ServerError(val code: Int) : MainError()
    data object Unknown : MainError()

    fun toUserMessage(): String = when (this) {
        is NoConnection -> "Sin conexión para actualizar el tablero."
        is ServerError -> "El servidor principal falló (Error $code)."
        is Unknown -> "Error inesperado en el tablero."
    }
}
