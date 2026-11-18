package ApunteIA.FrontEnd.data.remote

import ApunteIA.FrontEnd.data.datasource.model.AssessmentDto
import ApunteIA.FrontEnd.data.datasource.model.AttemptDto
import ApunteIA.FrontEnd.data.datasource.model.ChangePasswordRequestDto
import ApunteIA.FrontEnd.data.datasource.model.CourseDto
import ApunteIA.FrontEnd.data.datasource.model.CourseRequestDto
import ApunteIA.FrontEnd.data.datasource.model.CreateNoteRequestDto
import ApunteIA.FrontEnd.data.datasource.model.EnrollUserRequestDto
import ApunteIA.FrontEnd.data.datasource.model.EnrollmentDto
import ApunteIA.FrontEnd.data.datasource.model.GenerateAssessmentRequestDto
import ApunteIA.FrontEnd.data.datasource.model.ForgotPasswordRequestDto
import ApunteIA.FrontEnd.data.datasource.model.ResetPasswordRequestDto
import ApunteIA.FrontEnd.data.datasource.model.DirectResetPasswordRequestDto
import ApunteIA.FrontEnd.data.datasource.model.LoginRequestDto
import ApunteIA.FrontEnd.data.datasource.model.LoginResponseDto
import ApunteIA.FrontEnd.data.datasource.model.NoteDto
import ApunteIA.FrontEnd.data.datasource.model.RegisterRequestDto
import ApunteIA.FrontEnd.data.datasource.model.StartAttemptRequestDto
import ApunteIA.FrontEnd.data.datasource.model.SubmitAttemptRequestDto
import ApunteIA.FrontEnd.data.datasource.model.UpdateNoteRequestDto
import ApunteIA.FrontEnd.data.datasource.model.UserDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

/**
 * Endpoints reales del backend ApunteIA (Spring Boot en Render).
 * Todas las rutas, salvo login y registro, requieren JWT (lo agrega [AuthInterceptor]).
 */
interface ApiService {

    // ---------- Auth ----------

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): UserDto

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequestDto): Response<Unit>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDto): Response<Unit>

    @POST("auth/direct-reset-password")
    suspend fun directResetPassword(@Body request: DirectResetPasswordRequestDto): Response<Unit>

    @PUT("auth/password")
    suspend fun changePassword(@Body request: ChangePasswordRequestDto): Response<Unit>

    /** Petición liviana para "despertar" Render (plan gratuito) antes del login. */
    @GET("courses")
    suspend fun ping(): Response<ResponseBody>

    @GET("users/me/export")
    @Streaming
    suspend fun exportData(@Query("format") format: String): ResponseBody

    @DELETE("users/me")
    suspend fun deleteAccount(): Response<Unit>

    // ---------- Cursos e inscripciones ----------

    @GET("courses")
    suspend fun getCourses(@Query("language") language: String? = null): List<CourseDto>

    @GET("users/{id}/enrollments")
    suspend fun getUserEnrollments(@Path("id") userId: Long): List<EnrollmentDto>

    // Crear curso: autenticado. Actualizar/eliminar: creador o ADMIN.

    @POST("courses")
    suspend fun createCourse(@Body request: CourseRequestDto): CourseDto

    @PUT("courses/{id}")
    suspend fun updateCourse(@Path("id") id: Long, @Body request: CourseRequestDto): CourseDto

    @DELETE("courses/{id}")
    suspend fun deleteCourse(@Path("id") id: Long): Response<Unit>

    /** Estudiantes inscritos en un curso (incluye nombre y correo). */
    @GET("enrollments")
    suspend fun getCourseEnrollments(@Query("courseId") courseId: Long): List<EnrollmentDto>

    /** Quita a un estudiante del curso (borra su inscripción). */
    @DELETE("enrollments/{id}")
    suspend fun deleteEnrollment(@Path("id") enrollmentId: Long): Response<Unit>

    @POST("courses/{id}/enrollments")
    suspend fun enroll(
        @Path("id") courseId: Long,
        @Body request: EnrollUserRequestDto
    ): EnrollmentDto

    // ---------- Notas ----------

    @GET("notes")
    suspend fun getNotes(@Query("userId") userId: Long? = null): List<NoteDto>

    @GET("notes/{id}")
    suspend fun getNote(@Path("id") id: Long): NoteDto

    @POST("notes")
    suspend fun createNote(@Body request: CreateNoteRequestDto): NoteDto

    @PUT("notes/{id}")
    suspend fun updateNote(@Path("id") id: Long, @Body request: UpdateNoteRequestDto): NoteDto

    @DELETE("notes/{id}")
    suspend fun deleteNote(@Path("id") id: Long): Response<Unit>

    /** IA: genera el resumen de la nota (PENDING → COMPLETED / FAILED). */
    @POST("notes/{id}/process")
    suspend fun processNote(
        @Path("id") id: Long,
        @Query("language") language: String
    ): NoteDto

    /** IA: genera un quiz de opción múltiple a partir de la nota. */
    @POST("notes/{id}/assessments/generate")
    suspend fun generateAssessment(
        @Path("id") noteId: Long,
        @Body request: GenerateAssessmentRequestDto
    ): AssessmentDto

    // ---------- Evaluaciones e intentos ----------

    @GET("courses/{id}/assessments")
    suspend fun getCourseAssessments(
        @Path("id") courseId: Long,
        @Query("language") language: String? = null
    ): List<AssessmentDto>

    @GET("assessments/{id}")
    suspend fun getAssessment(
        @Path("id") id: Long,
        @Query("language") language: String? = null
    ): AssessmentDto

    @POST("assessments/{id}/attempts")
    suspend fun startAttempt(
        @Path("id") assessmentId: Long,
        @Body request: StartAttemptRequestDto
    ): AttemptDto

    @PATCH("attempts/{id}/submit")
    suspend fun submitAttempt(
        @Path("id") attemptId: Long,
        @Body request: SubmitAttemptRequestDto
    ): AttemptDto

    @GET("users/{id}/attempts")
    suspend fun getUserAttempts(@Path("id") userId: Long): List<AttemptDto>
}
