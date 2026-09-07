package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.model.Note
import javax.inject.Inject

/**
 * Caso de uso: aplica la regla de negocio de búsqueda + categoría sobre una
 * lista de notas. Vive en el dominio porque define qué significa que una
 * nota "coincida" con una búsqueda, independientemente de la UI.
 */
class FilterNotesUseCase @Inject constructor() {

    companion object {
        const val CATEGORY_ALL = "Todos"
    }

    operator fun invoke(
        notes: List<Note>,
        query: String,
        category: String
    ): List<Note> {
        return notes.filter { note ->
            val matchesSearch = query.isBlank() ||
                note.title.contains(query, ignoreCase = true) ||
                note.preview.contains(query, ignoreCase = true)
            val matchesCategory = category == CATEGORY_ALL || note.className == category
            matchesSearch && matchesCategory
        }
    }
}
