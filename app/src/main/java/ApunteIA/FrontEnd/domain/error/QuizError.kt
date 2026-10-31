package ApunteIA.FrontEnd.domain.error

sealed class QuizError : Exception() {
    data object NoConnection : QuizError()
    data class ServerError(val code: Int, val detail: String? = null) : QuizError()
    data object Unknown : QuizError()

    fun toUserMessage(): String = when (this) {
        is NoConnection -> "No hay conexión para cargar el cuestionario."
        is ServerError -> detail ?: "El servidor de cuestionarios falló (Error $code)."
        is Unknown -> "Ocurrió un error al cargar las preguntas."
    }
}