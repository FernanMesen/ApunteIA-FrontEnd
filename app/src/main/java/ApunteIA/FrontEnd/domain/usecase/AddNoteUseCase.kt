package ApunteIA.FrontEnd.domain.usecase
import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.repository.NoteRepository
import ApunteIA.FrontEnd.domain.util.Resource
import javax.inject.Inject

/**
 * Caso de uso: guardar la nota estructurada por la IA al terminar el flujo
 * de escaneo (pantalla "Revisar nota" -> "Guardar nota").
 */
class AddNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note): Resource<Unit> {
        return repository.addNote(note)
    }
}
