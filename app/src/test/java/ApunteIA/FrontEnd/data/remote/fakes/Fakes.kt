package ApunteIA.FrontEnd.fakes

import ApunteIA.FrontEnd.data.datasource.AuthDataSource
import ApunteIA.FrontEnd.data.datasource.CourseDataSource
import ApunteIA.FrontEnd.data.datasource.NoteDataSource
import ApunteIA.FrontEnd.data.datasource.QuizDataSource
import ApunteIA.FrontEnd.data.datasource.model.AssessmentDto
import ApunteIA.FrontEnd.data.datasource.model.AttemptAnswerDto
import ApunteIA.FrontEnd.data.datasource.model.AttemptDto
import ApunteIA.FrontEnd.data.datasource.model.CourseDto
import ApunteIA.FrontEnd.data.datasource.model.CreateNoteRequestDto
import ApunteIA.FrontEnd.data.datasource.model.EnrollmentDto
import ApunteIA.FrontEnd.data.datasource.model.LoginResponseDto
import ApunteIA.FrontEnd.data.datasource.model.NoteDto
import ApunteIA.FrontEnd.data.datasource.model.QuestionResponseDto
import ApunteIA.FrontEnd.data.datasource.model.UpdateNoteRequestDto
import ApunteIA.FrontEnd.data.datasource.model.UserDto
import ApunteIA.FrontEnd.data.repository.CourseCatalog
import ApunteIA.FrontEnd.data.repository.StudyDataLoader
import ApunteIA.FrontEnd.data.session.SessionStore
import ApunteIA.FrontEnd.domain.model.AuthSession
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

/** Error HTTP con el cuerpo `ErrorResponse` que manda el backend. */
fun httpError(code: Int, message: String? = null): HttpException {
    val body = if (message != null) """{"status":$code,"error":"x","message":"$message","path":"/x"}""" else ""
    return HttpException(Response.error<Any>(code, body.toResponseBody("application/json".toMediaType())))
}

class FakeSessionStore(var session: AuthSession? = null) : SessionStore {
    val expired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    var unauthorizedCalls = 0

    override val token: String? get() = session?.token
    override val userId: Long get() = session?.userId ?: 0L
    override val sessionExpired: SharedFlow<Unit> get() = expired
    override fun save(session: AuthSession) { this.session = session }
    override fun currentSession(): AuthSession? = session
    override fun isLoggedIn(): Boolean = session != null
    override fun clear() { session = null }
    override fun onUnauthorized() { unauthorizedCalls++; session = null }

    companion object {
        fun loggedIn(userId: Long = 2L, name: String = "Maya Rodríguez") = FakeSessionStore(
            AuthSession(token = "jwt", userName = name, userId = userId, email = "demo@apunteia.com", role = "STUDENT")
        )
    }
}

class FakeAuthDataSource : AuthDataSource {
    var loginResult: () -> LoginResponseDto = {
        LoginResponseDto(token = "jwt", userName = "Maya Rodríguez", expiresIn = 7200, userId = 2, email = "demo@apunteia.com", role = "STUDENT")
    }
    var registerResult: () -> UserDto = { UserDto(9, "new@apunteia.com", "Nuevo", "STUDENT", "ACTIVE") }
    var registered: Triple<String, String, String>? = null

    override suspend fun login(email: String, password: String): LoginResponseDto = loginResult()
    override suspend fun register(fullName: String, email: String, password: String): UserDto {
        registered = Triple(fullName, email, password)
        return registerResult()
    }
    override suspend fun ping() = Unit
}

class FakeCourseDataSource(
    var courses: List<CourseDto> = listOf(
        CourseDto(1, "EIF411", "Backend para plataformas móviles", "Persistencia"),
        CourseDto(2, "EIF412", "Bases de datos", "Modelado relacional"),
        CourseDto(3, "EIF413", "Programación distribuida", null)
    ),
    var enrollments: List<EnrollmentDto> = listOf(
        EnrollmentDto(10, 2, 1, "EIF411", "Backend para plataformas móviles", null),
        EnrollmentDto(11, 2, 2, "EIF412", "Bases de datos", null)
    )
) : CourseDataSource {
    var enrollError: Throwable? = null
    val enrolled = mutableListOf<Pair<Long, Long>>()

    override suspend fun getCourses(): List<CourseDto> = courses
    override suspend fun getEnrollments(userId: Long): List<EnrollmentDto> = enrollments
    override suspend fun enroll(courseId: Long, userId: Long): EnrollmentDto {
        enrollError?.let { throw it }
        enrolled += courseId to userId
        return EnrollmentDto(99, userId, courseId, null, null, null)
    }
}

class FakeNoteDataSource(var notes: MutableList<NoteDto> = mutableListOf()) : NoteDataSource {
    var processError: Throwable? = null
    var lastCreate: CreateNoteRequestDto? = null
    var lastUpdate: UpdateNoteRequestDto? = null
    val deleted = mutableListOf<Long>()

    override suspend fun getNotes(userId: Long): List<NoteDto> = notes.filter { it.userId == userId }
    override suspend fun getNote(id: Long): NoteDto = notes.first { it.id == id }
    override suspend fun createNote(request: CreateNoteRequestDto): NoteDto {
        lastCreate = request
        val dto = note(id = (notes.maxOfOrNull { it.id } ?: 0) + 1, title = request.title, content = request.content, courseId = request.courseId, userId = request.userId)
        notes += dto
        return dto
    }
    override suspend fun updateNote(id: Long, request: UpdateNoteRequestDto): NoteDto {
        lastUpdate = request
        val updated = getNote(id).copy(title = request.title, content = request.content, summary = request.summary)
        notes.replaceAll { if (it.id == id) updated else it }
        return updated
    }
    override suspend fun deleteNote(id: Long) { deleted += id; notes.removeAll { it.id == id } }
    override suspend fun processNote(id: Long): NoteDto {
        processError?.let { throw it }
        val processed = getNote(id).copy(summary = "- Punto clave", processingStatus = "COMPLETED")
        notes.replaceAll { if (it.id == id) processed else it }
        return processed
    }
    override suspend fun generateAssessment(noteId: Long, questionCount: Int, difficulty: String): AssessmentDto =
        assessment(id = 50, title = "Quiz IA - ${getNote(noteId).title}", questions = questionCount)

    companion object {
        fun note(
            id: Long,
            title: String = "Nota $id",
            content: String = "Contenido",
            summary: String? = null,
            courseId: Long = 1,
            userId: Long = 2,
            status: String = "PENDING",
            createdAt: String? = "2026-10-0${(id % 9) + 1}T10:00:00"
        ) = NoteDto(id, title, content, summary, userId, courseId, "EIF41$courseId", status, createdAt, createdAt)
    }
}

class FakeQuizDataSource : QuizDataSource {
    val assessments = mutableMapOf<Long, AssessmentDto>()
    val courseAssessments = mutableMapOf<Long, List<AssessmentDto>>()
    var attempts = mutableListOf<AttemptDto>()
    var startError: Throwable? = null
    var submitted: List<Int?>? = null
    var submitResult: (List<Int?>) -> AttemptDto = { answers ->
        attempt(id = 7, assessmentId = 50, status = "COMPLETED", score = 50.0, answers = answers.mapIndexed { i, a ->
            AttemptAnswerDto(i, a, a == 0, correctOptionIndex = 0)
        })
    }

    override suspend fun getAssessment(id: Long): AssessmentDto = assessments.getValue(id)
    override suspend fun getCourseAssessments(courseId: Long): List<AssessmentDto> = courseAssessments[courseId].orEmpty()
    override suspend fun startAttempt(assessmentId: Long, userId: Long): AttemptDto {
        startError?.let { throw it }
        return attempt(id = 7, assessmentId = assessmentId, status = "IN_PROGRESS", score = null)
    }
    override suspend fun submitAttempt(attemptId: Long, answers: List<Int?>): AttemptDto {
        submitted = answers
        return submitResult(answers)
    }
    override suspend fun getUserAttempts(userId: Long): List<AttemptDto> = attempts
}

fun assessment(id: Long, title: String, questions: Int = 2, courseId: Long = 1) = AssessmentDto(
    id = id,
    title = title,
    description = null,
    type = "QUIZ",
    difficulty = "MEDIUM",
    durationMinutes = questions * 2,
    courseId = courseId,
    courseName = "Backend para plataformas móviles",
    totalQuestions = questions,
    questions = (0 until questions).map { QuestionResponseDto(it, "Pregunta $it", listOf("A", "B", "C", "D")) }
)

fun attempt(
    id: Long,
    assessmentId: Long,
    status: String,
    score: Double?,
    title: String = "Quiz",
    completedAt: String? = if (status == "COMPLETED") "2026-10-05T12:00:00Z" else null,
    answers: List<AttemptAnswerDto> = emptyList()
) = AttemptDto(id, assessmentId, title, 2, status, score, "2026-10-05T11:50:00Z", completedAt, 600, answers)

/** Arma el loader real con fuentes de datos falsas. */
class TestBackend(
    val session: FakeSessionStore = FakeSessionStore.loggedIn(),
    val notes: FakeNoteDataSource = FakeNoteDataSource(),
    val courses: FakeCourseDataSource = FakeCourseDataSource(),
    val quizzes: FakeQuizDataSource = FakeQuizDataSource()
) {
    val catalog = CourseCatalog(courses)
    val loader = StudyDataLoader(session, notes, courses, quizzes, catalog)
}