package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.data.datasource.model.NoteDto
import ApunteIA.FrontEnd.data.remote.ApiService
import javax.inject.Inject

/**
 * Implementación de [NoteDataSource] que consume una API remota mediante Retrofit.
 */
class NoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : NoteDataSource {

    override suspend fun getNotes(): List<NoteDto> = apiService.getNotes()

    override suspend fun addNote(note: NoteDto) {
        apiService.createNote(note)
    }
}
