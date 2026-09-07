package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.NoteDataSource
import ApunteIA.FrontEnd.data.mapper.toDomain
import ApunteIA.FrontEnd.data.mapper.toDto
import ApunteIA.FrontEnd.domain.error.NoteError
import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.repository.NoteRepository
import ApunteIA.FrontEnd.domain.util.Resource
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

    override suspend fun getNotes(): Resource<List<Note>> = wrapExceptions {
        dataSource.getNotes().toDomain()
    }

    override suspend fun addNote(note: Note): Resource<Unit> = wrapExceptions {
        dataSource.addNote(note.toDto())
        Unit
    }

    private suspend fun <T> wrapExceptions(block: suspend () -> T): Resource<T> {
        return try {
            Resource.Success(block())
        } catch (e: IOException) {
            Resource.Error("No hay conexión a internet", e)
        } catch (e: HttpException) {
            val message = when (e.code()) {
                in 400..499 -> "Error del cliente (${e.code()})"
                else -> "Error del servidor (${e.code()})"
            }
            Resource.Error(message, e)
        } catch (e: Exception) {
            Resource.Error("Error desconocido", e)
        }
    }
}

