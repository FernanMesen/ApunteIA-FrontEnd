package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.NoteDataSource
import ApunteIA.FrontEnd.data.mapper.toDomain
import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.repository.NoteRepository

/**
 * Implementación real del contrato de dominio [NoteRepository]. Obtiene los
 * DTOs de la fuente de datos y los transforma a entidades de dominio con el
 * mapper antes de devolverlos.
 */
class NoteRepositoryImpl(
    private val dataSource: NoteDataSource
) : NoteRepository {

    override suspend fun getNotes(): List<Note> = dataSource.getNotes().toDomain()
}
