package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.error.NoteError
import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.repository.NoteRepository
import javax.inject.Inject

/**
 * Caso de uso: obtener todas las notas del estudiante. Devuelve un
 * [Result] para que quien lo llame (el ViewModel) pueda distinguir entre
 * éxito y [NoteError] sin usar try/catch por su cuenta.
 */
class GetNotesUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(): Result<List<Note>> {
        return try {
            Result.success(repository.getNotes())
        } catch (e: NoteError) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(NoteError.Unknown)
        }
    }
}
