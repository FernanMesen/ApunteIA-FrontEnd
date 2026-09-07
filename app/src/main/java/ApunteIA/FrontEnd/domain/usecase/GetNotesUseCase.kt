package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.repository.NoteRepository
import ApunteIA.FrontEnd.domain.util.Resource
import javax.inject.Inject

/**
 * Caso de uso: obtener todas las notas del estudiante. Devuelve un
 * [Resource] para que quien lo llame (el ViewModel) pueda distinguir entre
 * éxito y error sin usar try/catch por su cuenta.
 */
class GetNotesUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(): Resource<List<Note>> {
        return repository.getNotes()
    }
}
