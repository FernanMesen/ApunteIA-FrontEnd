package ApunteIA.FrontEnd.domain.model

/** Foto completa del estudio del usuario: base para el tablero, la auditoría y el historial. */
data class StudyOverview(
    val userName: String,
    val notes: List<Note>,
    val myCourses: List<CourseInfo>,
    val attempts: List<AssessmentAttempt>,
    val topics: List<Topic>
)