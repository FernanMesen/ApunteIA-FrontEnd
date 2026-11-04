package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.model.CourseInfo
import ApunteIA.FrontEnd.domain.model.CourseStudent
import ApunteIA.FrontEnd.domain.model.StudentCourseGrades
import ApunteIA.FrontEnd.domain.repository.CourseRepository
import ApunteIA.FrontEnd.domain.repository.QuizRepository
import ApunteIA.FrontEnd.domain.util.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

/** Crea (id = null) o actualiza un curso con su plan de estudios. Solo ADMIN. */
class SaveCourseUseCase @Inject constructor(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(
        id: Long?,
        code: String,
        name: String,
        description: String,
        syllabus: String
    ): Resource<CourseInfo> = repository.saveCourse(id, code, name, description, syllabus)
}

/**
 * Nota de un estudiante en un curso según [CourseGrades]: mejor intento por
 * evaluación del curso, promediado. Lo usan la lista y el detalle, así siempre coinciden.
 */
class GetStudentCourseGradesUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(courseId: Long, userId: Long): Resource<StudentCourseGrades> =
        when (val ids = quizRepository.getCourseAssessmentIds(courseId)) {
            is Resource.Success -> forAssessments(ids.data, userId)
            is Resource.Error -> ids
            is Resource.Loading -> ids
        }

    /** Variante para cuando ya se conocen las evaluaciones del curso (evita pedirlas por estudiante). */
    suspend fun forAssessments(courseAssessmentIds: Set<Long>, userId: Long): Resource<StudentCourseGrades> {
        // Curso sin evaluaciones (o usuario inválido): sin calificación y sin pedir nada al backend.
        if (courseAssessmentIds.isEmpty() || userId <= 0L) {
            return Resource.Success(StudentCourseGrades(finalGrade = null, attempts = emptyList()))
        }
        return when (val attempts = quizRepository.getUserAttempts(userId)) {
            is Resource.Success -> Resource.Success(
                StudentCourseGrades(
                    finalGrade = CourseGrades.finalGrade(courseAssessmentIds, attempts.data),
                    attempts = CourseGrades.courseAttempts(courseAssessmentIds, attempts.data)
                        .sortedByDescending { it.completedAt }
                )
            )
            is Resource.Error -> attempts
            is Resource.Loading -> attempts
        }
    }
}

/**
 * Estudiantes inscritos en un curso, cada uno con su nota del curso. Las evaluaciones
 * del curso se piden una vez y los intentos de los estudiantes, en paralelo.
 */
class GetCourseStudentsUseCase @Inject constructor(
    private val repository: CourseRepository,
    private val quizRepository: QuizRepository,
    private val getStudentGrades: GetStudentCourseGradesUseCase
) {
    suspend operator fun invoke(courseId: Long): Resource<List<CourseStudent>> {
        val students = when (val result = repository.getCourseStudents(courseId)) {
            is Resource.Success -> result.data
            is Resource.Error -> return result
            is Resource.Loading -> return result
        }
        // Si no se pueden leer las evaluaciones, la lista se muestra igual, sin notas.
        val assessmentIds = (quizRepository.getCourseAssessmentIds(courseId) as? Resource.Success)?.data
            ?: return Resource.Success(students)

        val graded = coroutineScope {
            students.map { student ->
                async {
                    val grades = getStudentGrades.forAssessments(assessmentIds, student.userId)
                    student.copy(averageGrade = (grades as? Resource.Success)?.data?.finalGrade)
                }
            }.awaitAll()
        }
        return Resource.Success(graded)
    }
}

/** Quita a un estudiante de un curso (borra la inscripción). Solo ADMIN. */
class RemoveStudentFromCourseUseCase @Inject constructor(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(enrollmentId: Long): Resource<Unit> = repository.removeStudent(enrollmentId)
}

/** Elimina un curso. Solo ADMIN. */
class DeleteCourseUseCase @Inject constructor(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(courseId: Long): Resource<String> = repository.deleteCourse(courseId)
}
