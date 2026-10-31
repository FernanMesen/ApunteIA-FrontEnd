package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.model.QuizDifficulty
import ApunteIA.FrontEnd.domain.repository.NoteRepository
import ApunteIA.FrontEnd.domain.repository.TextRecognizer
import ApunteIA.FrontEnd.domain.util.Resource
import java.io.File
import javax.inject.Inject

class GetNoteDetailUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: Long): Resource<Note> = repository.getNote(id)
}

class CreateNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(title: String, content: String, courseId: Long): Resource<Note> =
        repository.createNote(title.trim(), content.trim(), courseId)
}

class UpdateNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(
        id: Long,
        title: String,
        content: String,
        courseId: Long,
        summary: String?
    ): Resource<Note> = repository.updateNote(id, title.trim(), content.trim(), courseId, summary)
}

class DeleteNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: Long): Resource<Unit> = repository.deleteNote(id)
}

/** IA: resumen de la nota (OpenAI desde el backend). */
class ProcessNoteWithAiUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: Long): Resource<Note> = repository.processWithAi(id)
}

/** IA: genera un quiz de opción múltiple a partir de la nota. Devuelve el id de la evaluación. */
class GenerateQuizFromNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(
        noteId: Long,
        questionCount: Int = 5,
        difficulty: QuizDifficulty = QuizDifficulty.MEDIUM
    ): Resource<Long> = repository.generateQuiz(noteId, questionCount.coerceIn(1, 20), difficulty)
}

/** OCR de las páginas escaneadas. */
class RecognizeTextUseCase @Inject constructor(
    private val recognizer: TextRecognizer
) {
    suspend operator fun invoke(pages: List<File>): String = recognizer.recognize(pages)
}