package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.NoteDataSource
import ApunteIA.FrontEnd.data.mapper.toDomain
import ApunteIA.FrontEnd.data.mapper.toDto
import ApunteIA.FrontEnd.data.util.safeApiCall
import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.repository.NoteRepository
import ApunteIA.FrontEnd.domain.util.Resource
import javax.inject.Inject

/**
 * Implementación real del contrato de dominio [NoteRepository]. Obtiene los
 * DTOs de la fuente de datos y los transforma a entidades de dominio con el
 * mapper antes de devolverlos.
 */
class NoteRepositoryImpl @Inject constructor(
    private val dataSource: NoteDataSource
) : NoteRepository {

    override suspend fun getNotes(): Resource<List<Note>> = safeApiCall {
        dataSource.getNotes().toDomain()
    }

    override suspend fun addNote(note: Note): Resource<Unit> = safeApiCall {
        dataSource.addNote(note.toDto())
        Unit
    }
}

