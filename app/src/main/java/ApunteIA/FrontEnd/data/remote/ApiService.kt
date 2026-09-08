package ApunteIA.FrontEnd.data.remote

import ApunteIA.FrontEnd.data.datasource.model.LoginRequestDto
import ApunteIA.FrontEnd.data.datasource.model.LoginResponseDto
import ApunteIA.FrontEnd.data.datasource.model.NoteDto
import ApunteIA.FrontEnd.data.datasource.model.QuestionDto
import ApunteIA.FrontEnd.data.datasource.model.RecentActivityDto
import ApunteIA.FrontEnd.data.datasource.model.TopicDto
import ApunteIA.FrontEnd.data.datasource.model.UserStatsDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @GET("notes")
    suspend fun getNotes(): List<NoteDto>

    @POST("notes")
    suspend fun createNote(@Body note: NoteDto): NoteDto

    @GET("questions")
    suspend fun getQuestions(): List<QuestionDto>

    @GET("topics")
    suspend fun getTopics(): List<TopicDto>

    @POST("topics")
    suspend fun createTopic(@Body topic: TopicDto): TopicDto

    @GET("dashboard/stats")
    suspend fun getUserStats(): UserStatsDto

    @GET("dashboard/activity")
    suspend fun getRecentActivity(): List<RecentActivityDto>
}