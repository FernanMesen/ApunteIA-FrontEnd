package ApunteIA.FrontEnd.domain.model

/** Curso tal como existe en el backend (GET /courses). */
data class CourseInfo(
    val id: Long,
    val code: String,
    val name: String,
    val description: String
)