package ApunteIA.FrontEnd.domain.repository

import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.model.QuizDifficulty
import ApunteIA.FrontEnd.domain.util.Resource

/**
 * Contrato que expone el dominio para las notas del usuario actual. La capa `data`
 * lo implementa contra el backend (NoteRepositoryImpl); `domain` y `presentation`
 * solo conocen esta interfaz.
 */
interface NoteRepository {
    /** Notas del usuario con sesión iniciada. */
    suspend fun getNotes(): Resource<List<Note>>

    suspend fun getNote(id: Long): Resource<Note>

    suspend fun createNote(title: String, content: String, courseId: Long, noteDate: String? = null): Resource<Note>

    suspend fun updateNote(
        id: Long,
        title: String,
        content: String,
        courseId: Long,
        summary: String?
    ): Resource<Note>

    suspend fun deleteNote(id: Long): Resource<Unit>

    /** IA: genera el resumen de la nota. */
    suspend fun processWithAi(id: Long, language: String): Resource<Note>

    /** IA: genera un quiz a partir de la nota. Devuelve el id de la evaluación creada. */
    suspend fun generateQuiz(noteId: Long, questionCount: Int, difficulty: QuizDifficulty, language: String): Resource<Long>
}
