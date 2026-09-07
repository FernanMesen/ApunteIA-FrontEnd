package ApunteIA.FrontEnd.data.remote

import ApunteIA.FrontEnd.data.datasource.model.NoteDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("notes")
    suspend fun getNotes(): List<NoteDto>

    @POST("notes")
    suspend fun createNote(@Body note: NoteDto): NoteDto
}
