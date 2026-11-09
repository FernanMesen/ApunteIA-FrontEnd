package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.data.datasource.QuizDataSource
import ApunteIA.FrontEnd.data.datasource.model.EnrollmentDto
import ApunteIA.FrontEnd.data.repository.CourseRepositoryImpl
import ApunteIA.FrontEnd.data.repository.QuizRepositoryImpl
import ApunteIA.FrontEnd.domain.util.Resource
import ApunteIA.FrontEnd.fakes.TestBackend
import ApunteIA.FrontEnd.fakes.assessment
import ApunteIA.FrontEnd.fakes.attempt
import ApunteIA.FrontEnd.util.AiLanguagePreferenceManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

/** F13: notas de los estudiantes que ve el admin (lista y detalle). */
class CourseStudentGradesUseCaseTest {

    private val backend = TestBackend().apply {
        courses.courseEnrollments = listOf(
            EnrollmentDto(10, 2, 1, "EIF411", "Backend", null, "Ana Solís", "ana@una.cr"),
            EnrollmentDto(11, 3, 1, "EIF411", "Backend", null, "Zoe Mora", "zoe@una.cr"),
            EnrollmentDto(12, 3, 2, "EIF412", "BD", null, "Zoe Mora", "zoe@una.cr")
        )
        // Curso 1: quizzes 10 y 11. Curso 2: quiz 99. Curso 3: sin quizzes.
        quizzes.courseAssessments[1] = listOf(assessment(10, "Quiz A", courseId = 1), assessment(11, "Quiz B", courseId = 1))
        quizzes.courseAssessments[2] = listOf(assessment(99, "Quiz BD", courseId = 2))
        quizzes.attemptsByUser[2] = listOf(
            attempt(1, assessmentId = 10, status = "COMPLETED", score = 40.0, completedAt = "2026-10-01T10:00:00Z"),
            attempt(2, assessmentId = 10, status = "COMPLETED", score = 80.0, completedAt = "2026-10-03T10:00:00Z"),
            attempt(3, assessmentId = 11, status = "COMPLETED", score = 60.0, completedAt = "2026-10-02T10:00:00Z"),
            attempt(4, assessmentId = 11, status = "IN_PROGRESS", score = null),
            attempt(5, assessmentId = 99, status = "COMPLETED", score = 100.0) // otra materia
        )
        quizzes.attemptsByUser[3] = listOf(
            attempt(6, assessmentId = 99, status = "COMPLETED", score = 100.0)
        )
    }

    private fun useCases(quizDataSource: QuizDataSource = backend.quizzes): Pair<GetCourseStudentsUseCase, GetStudentCourseGradesUseCase> {
        val quizRepository = QuizRepositoryImpl(quizDataSource, backend.loader, AiLanguagePreferenceManager(null))
        val courseRepository = CourseRepositoryImpl(backend.courses, backend.loader, backend.catalog, quizDataSource)
        val grades = GetStudentCourseGradesUseCase(quizRepository)
        return GetCourseStudentsUseCase(courseRepository, quizRepository, grades) to grades
    }

    private fun gradesByName(result: Resource<List<ApunteIA.FrontEnd.domain.model.CourseStudent>>) =
        (result as Resource.Success).data.associate { it.fullName to it.averageGrade }

    @Test
    fun `each student gets their own grade from the quizzes of that course`() = runBlocking {
        val (getStudents, _) = useCases()

        val grades = gradesByName(getStudents(1))

        // Ana: mejor de A (80) y B (60) → 70; el 100 de la otra materia no cuenta.
        assertEquals(70.0, grades.getValue("Ana Solís")!!, 0.001)
        // Zoe solo hizo el quiz de BD: en este curso no tiene nota.
        assertNull(grades.getValue("Zoe Mora"))
    }

    @Test
    fun `changing the course changes the grades`() = runBlocking {
        val (getStudents, _) = useCases()

        val grades = gradesByName(getStudents(2))

        assertEquals(mapOf("Zoe Mora" to 100.0), grades)
    }

    @Test
    fun `course without quizzes shows no grade and does not request attempts`() = runBlocking {
        backend.courses.courseEnrollments = listOf(
            EnrollmentDto(20, 2, 3, "EIF413", "Distribuida", null, "Ana Solís", "ana@una.cr")
        )
        val (getStudents, _) = useCases()

        val grades = gradesByName(getStudents(3))

        assertEquals(mapOf<String, Double?>("Ana Solís" to null), grades)
        assertTrue(backend.quizzes.userAttemptRequests.isEmpty())
    }

    @Test
    fun `student detail matches the list and lists only completed attempts of the course`() = runBlocking {
        val (getStudents, getGrades) = useCases()
        val listGrade = gradesByName(getStudents(1)).getValue("Ana Solís")

        val detail = (getGrades(courseId = 1, userId = 2) as Resource.Success).data

        assertEquals(listGrade!!, detail.finalGrade!!, 0.001)
        // Sin el intento en curso ni el de la otra materia; del más reciente al más antiguo.
        assertEquals(listOf(2, 3, 1), detail.attempts.map { it.id })
    }

    @Test
    fun `students list still loads when the course quizzes cannot be read`() = runBlocking {
        val failing = object : QuizDataSource by backend.quizzes {
            override suspend fun getCourseAssessments(courseId: Long, language: String?) = throw IOException()
        }
        val (getStudents, _) = useCases(failing)

        val grades = gradesByName(getStudents(1))

        assertEquals(setOf("Ana Solís", "Zoe Mora"), grades.keys)
        assertTrue(grades.values.all { it == null })
    }
}
