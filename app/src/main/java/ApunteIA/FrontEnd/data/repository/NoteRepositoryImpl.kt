package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.NoteDataSource
import ApunteIA.FrontEnd.data.mapper.toDomain
import ApunteIA.FrontEnd.data.mapper.toDto
import ApunteIA.FrontEnd.domain.error.NoteError
import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.repository.NoteRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Implementación real del contrato de dominio [NoteRepository]. Obtiene los
 * DTOs de la fuente de datos y los transforma a entidades de dominio con el
 * mapper antes de devolverlos.
 */
class NoteRepositoryImpl @Inject constructor(
    private val dataSource: NoteDataSource
) : NoteRepository {

    override suspend fun getNotes(): List<Note> = wrapExceptions {
        dataSource.getNotes().toDomain()
    }

    override suspend fun addNote(note: Note) = wrapExceptions {
        dataSource.addNote(note.toDto())
    }

    private suspend fun <T> wrapExceptions(block: suspend () -> T): T {
        return try {
            block()
        } catch (e: IOException) {
            throw NoteError.NoConnection
        } catch (e: HttpException) {
            when (e.code()) {
                in 400..499 -> throw NoteError.ClientError(e.code())
                else -> throw NoteError.ServerError(e.code())
            }
        } catch (e: Exception) {
            throw NoteError.Unknown
        }
    }
}

