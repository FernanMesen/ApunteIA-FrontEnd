package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.data.datasource.model.AssessmentDto
import ApunteIA.FrontEnd.data.datasource.model.CreateNoteRequestDto
import ApunteIA.FrontEnd.data.datasource.model.NoteDto
import ApunteIA.FrontEnd.data.datasource.model.UpdateNoteRequestDto

/**
 * Fuente de datos remota de notas (backend ApunteIA).
 */
interface NoteDataSource {
    suspend fun getNotes(userId: Long): List<NoteDto>
    suspend fun getNote(id: Long): NoteDto
    suspend fun createNote(request: CreateNoteRequestDto): NoteDto
    suspend fun updateNote(id: Long, request: UpdateNoteRequestDto): NoteDto
    suspend fun deleteNote(id: Long)
    suspend fun processNote(id: Long): NoteDto
    suspend fun generateAssessment(noteId: Long, questionCount: Int, difficulty: String): AssessmentDto
}