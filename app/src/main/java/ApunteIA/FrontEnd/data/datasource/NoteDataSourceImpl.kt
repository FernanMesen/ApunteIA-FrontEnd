package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.data.datasource.model.AssessmentDto
import ApunteIA.FrontEnd.data.datasource.model.CreateNoteRequestDto
import ApunteIA.FrontEnd.data.datasource.model.GenerateAssessmentRequestDto
import ApunteIA.FrontEnd.data.datasource.model.NoteDto
import ApunteIA.FrontEnd.data.datasource.model.UpdateNoteRequestDto
import ApunteIA.FrontEnd.data.remote.ApiService
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Implementación de [NoteDataSource] que consume el backend mediante Retrofit.
 */
class NoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : NoteDataSource {

    override suspend fun getNotes(userId: Long): List<NoteDto> = apiService.getNotes(userId)

    override suspend fun getNote(id: Long): NoteDto = apiService.getNote(id)

    override suspend fun createNote(request: CreateNoteRequestDto): NoteDto = apiService.createNote(request)

    override suspend fun updateNote(id: Long, request: UpdateNoteRequestDto): NoteDto =
        apiService.updateNote(id, request)

    override suspend fun deleteNote(id: Long) {
        val response = apiService.deleteNote(id)
        if (!response.isSuccessful && response.code() != 404) throw HttpException(response)
    }

    override suspend fun processNote(id: Long): NoteDto = apiService.processNote(id)

    override suspend fun generateAssessment(noteId: Long, questionCount: Int, difficulty: String): AssessmentDto =
        apiService.generateAssessment(noteId, GenerateAssessmentRequestDto(questionCount, difficulty))
}