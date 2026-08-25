package ApunteIA.FrontEnd.domain.repository

import ApunteIA.FrontEnd.domain.model.Note

/**
 * Contrato que expone el dominio para obtener notas, sin importar si detrás
 * hay datos locales de ejemplo, una base de datos o una API remota.
 *
 * La capa `data` (ApunteIA.FrontEnd.data.repository.NoteRepositoryImpl) es la
 * encargada de implementar este contrato; `domain` y `presentation` solo
 * conocen esta interfaz.
 */
interface NoteRepository {
    suspend fun getNotes(): List<Note>

    /**
     * Guarda una nota nueva (p.ej. la generada por IA al final de un escaneo).
     */
    suspend fun addNote(note: Note)
}