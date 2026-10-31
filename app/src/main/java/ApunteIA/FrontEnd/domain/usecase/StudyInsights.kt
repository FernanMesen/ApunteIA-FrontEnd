package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.model.AssessmentAttempt
import ApunteIA.FrontEnd.domain.model.CourseInfo
import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.model.NoteStatus
import ApunteIA.FrontEnd.domain.model.ProcessingStatus
import ApunteIA.FrontEnd.domain.model.RecentActivity
import ApunteIA.FrontEnd.domain.model.Topic
import ApunteIA.FrontEnd.domain.model.TopicStatus
import ApunteIA.FrontEnd.domain.model.UserStats
import ApunteIA.FrontEnd.domain.util.IsoDates

/**
 * Reglas de negocio puras para la auditoría y el tablero. El backend no tiene
 * "temas" ni "estadísticas": se calculan aquí a partir de las notas, los cursos
 * inscritos y los intentos de quiz del usuario.
 *
 * Un quiz generado con IA se titula "Quiz IA - <título de la nota>" (o con "(2)",
 * "(3)"... si se repite), así se relaciona cada intento con su nota.
 */
object StudyInsights {

    /** Puntaje mínimo (0..100) para considerar un tema "Sólido". */
    const val SOLID_THRESHOLD = 70

    /** Progreso que se da a una nota resumida con IA pero aún no evaluada. */
    const val SUMMARIZED_PROGRESS = 30

    private const val AI_QUIZ_PREFIX = "Quiz IA - "

    fun isCompleted(attempt: AssessmentAttempt): Boolean = attempt.completedAt.isNotBlank()

    fun quizBelongsToNote(assessmentTitle: String, noteTitle: String): Boolean {
        val base = AI_QUIZ_PREFIX + noteTitle.trim()
        return assessmentTitle == base || assessmentTitle.startsWith("$base (")
    }

    /** Mejor puntaje de los quizzes de esta nota, o null si nunca se evaluó. */
    fun bestScoreForNote(note: Note, attempts: List<AssessmentAttempt>): Int? =
        attempts
            .filter { isCompleted(it) && quizBelongsToNote(it.assessmentTitle, note.title) }
            .maxOfOrNull { it.scorePercent }

    /** Dominio que se muestra en la tarjeta de la nota. */
    fun noteMastery(note: Note, attempts: List<AssessmentAttempt>): NoteStatus {
        val best = bestScoreForNote(note, attempts) ?: return NoteStatus.WEAK
        return if (best >= SOLID_THRESHOLD) NoteStatus.SOLID else NoteStatus.WEAK
    }

    fun buildTopics(
        notes: List<Note>,
        myCourses: List<CourseInfo>,
        attempts: List<AssessmentAttempt>
    ): List<Topic> {
        val noteTopics = notes.map { note ->
            val best = bestScoreForNote(note, attempts)
            val (status, progress) = when {
                best != null && best >= SOLID_THRESHOLD -> TopicStatus.SOLID to best
                best != null -> TopicStatus.WEAK to best
                note.processingStatus == ProcessingStatus.COMPLETED -> TopicStatus.WEAK to SUMMARIZED_PROGRESS
                else -> TopicStatus.MISSING to 0
            }
            Topic(
                id = note.id,
                title = note.title,
                className = note.className,
                status = status,
                progress = progress
            )
        }
        val coursesWithoutNotes = myCourses
            .filter { course -> notes.none { it.courseId == course.id } }
            .map { course ->
                Topic(
                    id = -course.id.toInt(),
                    title = "Sin apuntes todavía",
                    className = course.name,
                    status = TopicStatus.MISSING,
                    progress = 0
                )
            }
        return noteTopics + coursesWithoutNotes
    }

    /** Días seguidos con actividad (notas o quizzes) hasta hoy, o hasta ayer si hoy aún no hay. */
    fun streakDays(activityMillis: List<Long>, now: Long = System.currentTimeMillis()): Int {
        if (activityMillis.isEmpty()) return 0
        val days = activityMillis.map { IsoDates.dayKey(it) }.toSet()
        val dayMs = 24L * 60 * 60 * 1000
        var cursor = IsoDates.startOfDay(now) + dayMs / 2
        if (IsoDates.dayKey(cursor) !in days) cursor -= dayMs
        var streak = 0
        while (IsoDates.dayKey(cursor) in days) {
            streak++
            cursor -= dayMs
        }
        return streak
    }

    fun activityMillis(notes: List<Note>, attempts: List<AssessmentAttempt>): List<Long> =
        notes.mapNotNull { IsoDates.parse(it.createdAtIso) } +
                attempts.filter { isCompleted(it) }.mapNotNull { IsoDates.parse(it.completedAt) }

    fun userStats(
        userName: String,
        notes: List<Note>,
        topics: List<Topic>,
        attempts: List<AssessmentAttempt>,
        now: Long = System.currentTimeMillis()
    ): UserStats = UserStats(
        userName = userName,
        streakDays = streakDays(activityMillis(notes, attempts), now),
        progressPercent = if (topics.isEmpty()) 0 else topics.sumOf { it.progress } / topics.size,
        notesCount = notes.size,
        pendingTopicsCount = topics.count { it.status != TopicStatus.SOLID },
        quizzesDoneCount = attempts.count { isCompleted(it) }
    )

    fun recentActivity(
        notes: List<Note>,
        attempts: List<AssessmentAttempt>,
        limit: Int = 5,
        now: Long = System.currentTimeMillis()
    ): List<RecentActivity> {
        data class Event(val millis: Long, val activity: RecentActivity)

        val noteEvents = notes.mapNotNull { note ->
            val millis = IsoDates.parse(note.createdAtIso) ?: return@mapNotNull null
            Event(
                millis,
                RecentActivity(
                    title = "Apunte guardado · ${note.title}",
                    subtitle = note.className,
                    time = IsoDates.relative(note.createdAtIso, now)
                )
            )
        }
        val quizEvents = attempts.filter { isCompleted(it) }.mapNotNull { attempt ->
            val millis = IsoDates.parse(attempt.completedAt) ?: return@mapNotNull null
            Event(
                millis,
                RecentActivity(
                    title = "Quiz completado · ${attempt.assessmentTitle}",
                    subtitle = "Nota: ${attempt.scorePercent}%",
                    time = IsoDates.relative(attempt.completedAt, now)
                )
            )
        }
        return (noteEvents + quizEvents)
            .sortedByDescending { it.millis }
            .take(limit)
            .map { it.activity }
    }
}