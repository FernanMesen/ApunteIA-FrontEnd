package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.data.datasource.model.NoteDto

/**
 * Implementación local/en memoria de [NoteDataSource], usada mientras no hay
 * backend real. Delega los datos crudos a [NoteProvider].
 */
class NoteDataSourceImpl : NoteDataSource {

    override suspend fun getNotes(): List<NoteDto> = NoteProvider.notes

    override suspend fun addNote(note: NoteDto) = NoteProvider.addNote(note)
}