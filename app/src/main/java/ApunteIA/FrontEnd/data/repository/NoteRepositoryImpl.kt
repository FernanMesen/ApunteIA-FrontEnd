package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.NoteDataSource
import ApunteIA.FrontEnd.data.datasource.model.CreateNoteRequestDto
import ApunteIA.FrontEnd.data.datasource.model.UpdateNoteRequestDto
import ApunteIA.FrontEnd.data.mapper.toDomain
import ApunteIA.FrontEnd.data.mapper.toNoteError
import ApunteIA.FrontEnd.data.util.safeApiCall
import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.model.QuizDifficulty
import ApunteIA.FrontEnd.domain.repository.NoteRepository
import ApunteIA.FrontEnd.domain.util.Resource
import javax.inject.Inject

/**
 * Implementación real del contrato de dominio [NoteRepository] contra el backend.
 * Las operaciones de IA (resumen y quiz) las ejecuta el backend con OpenAI; la app
 * nunca ve la API key.
 */
class NoteRepositoryImpl @Inject constructor(
    private val dataSource: NoteDataSource,
    private val loader: StudyDataLoader,
    private val catalog: CourseCatalog
) : NoteRepository {

    private val errorMapper: (Throwable) -> String = { it.toNoteError().toUserMessage() }

    override suspend fun getNotes(): Resource<List<Note>> = safeApiCall(errorMapper) {
        loader.myNotes()
    }

    override suspend fun getNote(id: Long): Resource<Note> = safeApiCall(errorMapper) {
        // Se busca dentro de "mis notas" para conservar el número de clase y el dominio.
        loader.myNotes().firstOrNull { it.id.toLong() == id }
            ?: dataSource.getNote(id).toDomain(catalog.byId())
    }

    override suspend fun createNote(title: String, content: String, courseId: Long): Resource<Note> =
        safeApiCall(errorMapper) {
            val request = CreateNoteRequestDto(
                title = title,
                content = content,
                userId = loader.requireUserId(),
                courseId = courseId
            )
            loader.invalidate()
            dataSource.createNote(request).toDomain(catalog.byId())
        }

    override suspend fun updateNote(
        id: Long,
        title: String,
        content: String,
        courseId: Long,
        summary: String?
    ): Resource<Note> = safeApiCall(errorMapper) {
        val request = UpdateNoteRequestDto(
            title = title,
            content = content,
            userId = loader.requireUserId(),
            courseId = courseId,
            summary = summary
        )
        loader.invalidate()
        dataSource.updateNote(id, request).toDomain(catalog.byId())
    }

    override suspend fun deleteNote(id: Long): Resource<Unit> = safeApiCall(errorMapper) {
        loader.invalidate()
        dataSource.deleteNote(id)
    }

    override suspend fun processWithAi(id: Long, language: String): Resource<Note> = safeApiCall(errorMapper) {
        loader.invalidate()
        dataSource.processNote(id, language).toDomain(catalog.byId())
    }

    override suspend fun generateQuiz(
        noteId: Long,
        questionCount: Int,
        difficulty: QuizDifficulty,
        language: String
    ): Resource<Long> = safeApiCall(errorMapper) {
        dataSource.generateAssessment(noteId, questionCount, difficulty.name, language).id
    }
}
