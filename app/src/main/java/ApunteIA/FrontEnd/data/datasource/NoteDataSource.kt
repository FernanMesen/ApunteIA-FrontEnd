package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.data.datasource.model.NoteDto

/**
 * Fuente de datos de notas.
 */
interface NoteDataSource {
    suspend fun getNotes(): List<NoteDto>
    suspend fun addNote(note: NoteDto)
}