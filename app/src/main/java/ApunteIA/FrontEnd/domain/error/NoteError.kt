package ApunteIA.FrontEnd.domain.error

/**
 * Errores de dominio que puede producir el feature de Notas. Al ser un tipo del
 * dominio, ni `data` ni `presentation` dependen de excepciones concretas de
 * Android/Kotlin: solo de este contrato.
 */
sealed class NoteError : Exception() {
    data object NoConnection : NoteError()
    data class ClientError(val code: Int, val detail: String? = null) : NoteError()
    data class ServerError(val code: Int, val detail: String? = null) : NoteError()
    data object InvalidData : NoteError()
    data object Unknown : NoteError()

    fun toUserMessage(): String = when (this) {
        is NoConnection -> "No hay conexión a internet. Por favor, verifica tu red."
        is ClientError -> detail ?: "Hubo un problema con la solicitud (Error $code)."
        is ServerError -> when (code) {
            502 -> detail ?: "La IA no pudo procesar la nota. Intenta de nuevo."
            503 -> detail ?: "La IA no está disponible en este momento."
            else -> detail ?: "El servidor de notas está teniendo problemas (Error $code)."
        }
        is InvalidData -> "Los datos recibidos no son válidos."
        is Unknown -> "Ocurrió un error inesperado al gestionar tus notas."
    }
}