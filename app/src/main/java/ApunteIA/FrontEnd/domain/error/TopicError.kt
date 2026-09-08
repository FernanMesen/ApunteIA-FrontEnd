package ApunteIA.FrontEnd.domain.error

sealed class TopicError : Exception() {
    data object DataUnavailable : TopicError()
    data object Unknown : TopicError()

    fun toUserMessage(): String = when (this) {
        is DataUnavailable -> "No se pudieron cargar las materias en este momento."
        is Unknown -> "Ocurrió un error inesperado con los temas de estudio."
    }
}
