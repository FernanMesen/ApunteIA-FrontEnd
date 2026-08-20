package ApunteIA.FrontEnd.domain.error

/**
 * Errores de dominio que puede producir el feature de Notas al obtener
 * datos. Al ser un tipo del dominio, ni `data` ni `presentation` dependen
 * de excepciones concretas de Android/Kotlin: solo de este contrato.
 */
sealed class NoteError : Exception() {
    /** La fuente de datos (local o remota) no pudo entregar las notas. */
    data object DataUnavailable : NoteError()

    /** Cualquier otro error no anticipado. */
    data object Unknown : NoteError()
}
