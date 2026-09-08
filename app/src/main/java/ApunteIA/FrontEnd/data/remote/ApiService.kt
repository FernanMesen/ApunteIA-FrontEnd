package ApunteIA.FrontEnd.data.remote

import ApunteIA.FrontEnd.data.datasource.model.NoteDto
import ApunteIA.FrontEnd.data.datasource.model.TopicDto
import ApunteIA.FrontEnd.domain.model.Question
import ApunteIA.FrontEnd.domain.model.RecentActivity
import ApunteIA.FrontEnd.domain.model.UserStats
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("notes")
    suspend fun getNotes(): List<NoteDto>

    @POST("notes")
    suspend fun createNote(@Body note: NoteDto): NoteDto

    @GET("topics")
    suspend fun getTopics(): List<TopicDto>

    @GET("questions")
    suspend fun getQuestions(): List<Question>

    @GET("userStats")
    suspend fun getUserStats(): UserStats

    @GET("recentActivity")
    suspend fun getRecentActivity(): List<RecentActivity>
}
