package ApunteIA.FrontEnd.domain.error

import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.util.L10n

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
        is NoConnection -> L10n.getString(R.string.note_error_connection)
        is ClientError -> detail ?: L10n.getString(R.string.note_error_client, code)
        is ServerError -> when (code) {
            502 -> detail ?: L10n.getString(R.string.note_error_502)
            503 -> detail ?: L10n.getString(R.string.note_error_503)
            else -> detail ?: L10n.getString(R.string.note_error_server, code)
        }
        is InvalidData -> L10n.getString(R.string.note_error_invalid_data)
        is Unknown -> L10n.getString(R.string.note_error_unknown)
    }
}
