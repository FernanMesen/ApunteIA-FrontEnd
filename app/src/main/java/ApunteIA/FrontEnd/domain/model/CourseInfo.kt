package ApunteIA.FrontEnd.domain.model

/** Curso tal como existe en el backend (GET /courses). */
data class CourseInfo(
    val id: Long,
    val code: String,
    val name: String,
    val description: String,
    /** Plan de estudios: un tema por línea ("" si el curso no tiene). */
    val syllabus: String = ""
) {
    /** Temas del plan de estudios, sin líneas vacías ni viñetas. */
    val syllabusTopics: List<String>
        get() = syllabus.lines()
            .map { it.trim().removePrefix("-").removePrefix("•").trim() }
            .filter { it.isNotEmpty() }
}

/** Estudiante inscrito en un curso (vista del administrador). */
data class CourseStudent(
    val enrollmentId: Long,
    val userId: Long,
    val fullName: String,
    val email: String,
    val averageGrade: Double? = null
)

/** Nota de un estudiante en un curso y los intentos que la forman (ver CourseGrades). */
data class StudentCourseGrades(
    /** Promedio del mejor intento de cada evaluación del curso; null = sin calificación. */
    val finalGrade: Double?,
    /** Intentos completados de evaluaciones del curso, del más reciente al más antiguo. */
    val attempts: List<AssessmentAttempt>
)
