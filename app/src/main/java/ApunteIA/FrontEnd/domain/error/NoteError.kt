package ApunteIA.FrontEnd.domain.error

/**
 * Errores de dominio que puede producir el feature de Notas al obtener
 * datos. Al ser un tipo del dominio, ni `data` ni `presentation` dependen
 * de excepciones concretas de Android/Kotlin: solo de este contrato.
 */
sealed class NoteError : Exception() {
    data object NoConnection : NoteError()
    data class ClientError(val code: Int) : NoteError()
    data class ServerError(val code: Int) : NoteError()
    data object InvalidData : NoteError()
    data object Unknown : NoteError()
}
