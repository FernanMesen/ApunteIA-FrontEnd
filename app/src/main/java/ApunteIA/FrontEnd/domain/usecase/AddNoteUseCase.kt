package ApunteIA.FrontEnd.domain.usecase
import ApunteIA.FrontEnd.domain.error.NoteError
import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.repository.NoteRepository
/**
 * Caso de uso: guardar la nota estructurada por la IA al terminar el flujo
 * de escaneo (pantalla "Revisar nota" -> "Guardar nota").
 */
class AddNoteUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note): Result<Unit> {
        return try {
            repository.addNote(note)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(NoteError.Unknown)
        }
    }
}