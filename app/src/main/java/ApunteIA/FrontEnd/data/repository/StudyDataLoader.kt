package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.CourseDataSource
import ApunteIA.FrontEnd.data.datasource.NoteDataSource
import ApunteIA.FrontEnd.data.datasource.QuizDataSource
import ApunteIA.FrontEnd.data.mapper.toDomain
import ApunteIA.FrontEnd.data.session.SessionStore
import ApunteIA.FrontEnd.data.util.NotLoggedInException
import ApunteIA.FrontEnd.domain.model.AssessmentAttempt
import ApunteIA.FrontEnd.domain.model.CourseInfo
import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.model.StudyOverview
import ApunteIA.FrontEnd.domain.usecase.StudyInsights
import ApunteIA.FrontEnd.util.LanguageManager
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/** Catálogo de cursos en memoria (GET /courses cambia muy poco). */
@Singleton
class CourseCatalog @Inject constructor(
    private val dataSource: CourseDataSource
) {
    private val mutex = Mutex()
    private val cache = mutableMapOf<String, List<CourseInfo>>()

    suspend fun all(forceRefresh: Boolean = false): List<CourseInfo> = mutex.withLock {
        val currentLang = LanguageManager.getCurrentLanguageCode()
        val cached = cache[currentLang]
        if (!forceRefresh && cached != null) {
            cached
        } else {
            dataSource.getCourses(currentLang).toDomain().also { cache[currentLang] = it }
        }
    }

    suspend fun byId(): Map<Long, CourseInfo> = all().associateBy { it.id }
}

/**
 * Carga en paralelo lo que necesitan varias pantallas (notas, cursos inscritos e
 * intentos del usuario actual). Guarda la última foto unos segundos para que el
 * tablero no repita las mismas peticiones al pedir estadísticas y actividad.
 */
@Singleton
class StudyDataLoader @Inject constructor(
    private val sessionManager: SessionStore,
    private val noteDataSource: NoteDataSource,
    private val courseDataSource: CourseDataSource,
    private val quizDataSource: QuizDataSource,
    private val catalog: CourseCatalog
) {
    private var lastOverview: StudyOverview? = null
    private var lastOverviewAt: Long = 0L

    fun requireUserId(): Long {
        val userId = sessionManager.userId
        if (userId <= 0L || sessionManager.token == null) throw NotLoggedInException()
        return userId
    }

    fun userName(): String = sessionManager.currentSession()?.userName.orEmpty()

    suspend fun myCourses(): List<CourseInfo> = coroutineScope {
        val userId = requireUserId()
        val enrollments = async { courseDataSource.getEnrollments(userId) }
        val catalogById = async { runCatching { catalog.byId() }.getOrDefault(emptyMap()) }
        val byId = catalogById.await()
        enrollments.await().map { enrollment ->
            val known = byId[enrollment.courseId]
            CourseInfo(
                id = enrollment.courseId,
                code = known?.code.orEmpty().ifBlank { enrollment.courseCode.orEmpty() },
                name = known?.name?.takeIf { it.isNotBlank() } ?: enrollment.courseName ?: "Curso ${enrollment.courseId}",
                description = known?.description.orEmpty()
            )
        }.distinctBy { it.id }
    }

    suspend fun myAttempts(): List<AssessmentAttempt> =
        quizDataSource.getUserAttempts(requireUserId()).map { it.toDomain() }

    /** Notas del usuario; los intentos son opcionales (sirven para el dominio Débil/Sólido). */
    suspend fun myNotes(attempts: List<AssessmentAttempt>? = null): List<Note> = coroutineScope {
        val userId = requireUserId()
        val notes = async { noteDataSource.getNotes(userId) }
        val courses = async { catalog.byId() }
        val userAttempts = attempts ?: async {
            runCatching { myAttempts() }.getOrDefault(emptyList())
        }.await()
        notes.await().toDomain(courses.await()) { note ->
            StudyInsights.noteMastery(note, userAttempts)
        }
    }

    suspend fun overview(maxAgeMillis: Long = 0L): StudyOverview {
        val cached = lastOverview
        val now = System.currentTimeMillis()
        if (cached != null && maxAgeMillis > 0 && now - lastOverviewAt <= maxAgeMillis) return cached

        val overview = coroutineScope {
            val attempts = async { myAttempts() }
            val courses = async { myCourses() }
            val attemptList = attempts.await()
            val notes = myNotes(attemptList)
            val myCourses = courses.await()
            StudyOverview(
                userName = userName(),
                notes = notes,
                myCourses = myCourses,
                attempts = attemptList,
                topics = StudyInsights.buildTopics(notes, myCourses, attemptList)
            )
        }
        lastOverview = overview
        lastOverviewAt = System.currentTimeMillis()
        return overview
    }

    fun invalidate() {
        lastOverview = null
    }
}