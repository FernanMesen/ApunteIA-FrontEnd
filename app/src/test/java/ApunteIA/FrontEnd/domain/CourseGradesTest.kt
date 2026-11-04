package ApunteIA.FrontEnd.domain

import ApunteIA.FrontEnd.domain.model.AssessmentAttempt
import ApunteIA.FrontEnd.domain.usecase.CourseGrades
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Regla de la nota del curso (F13): mejor intento por evaluación, promediado. */
class CourseGradesTest {

    private var nextId = 1

    private fun completed(assessmentId: Long, score: Int) = AssessmentAttempt(
        id = nextId++,
        assessmentId = assessmentId.toString(),
        assessmentTitle = "Quiz $assessmentId",
        scorePercent = score,
        completedAt = "2026-10-05T12:00:00Z"
    )

    /** El mapper deja completedAt vacío cuando el intento no está COMPLETED. */
    private fun inProgress(assessmentId: Long) = AssessmentAttempt(
        id = nextId++,
        assessmentId = assessmentId.toString(),
        scorePercent = 0,
        completedAt = ""
    )

    @Test
    fun `course without assessments has no grade even if the student has attempts elsewhere`() {
        val attempts = listOf(completed(10, 90), completed(11, 70))

        assertNull(CourseGrades.finalGrade(emptySet(), attempts))
        assertTrue(CourseGrades.courseAttempts(emptySet(), attempts).isEmpty())
    }

    @Test
    fun `attempts from another course are ignored`() {
        val attempts = listOf(completed(10, 50), completed(99, 100))

        assertEquals(50.0, CourseGrades.finalGrade(setOf(10L), attempts)!!, 0.001)
        assertEquals(listOf("10"), CourseGrades.courseAttempts(setOf(10L), attempts).map { it.assessmentId })
    }

    @Test
    fun `two attempts of the same quiz count only the best one`() {
        val attempts = listOf(completed(10, 40), completed(10, 80))

        assertEquals(80.0, CourseGrades.finalGrade(setOf(10L), attempts)!!, 0.001)
    }

    @Test
    fun `two quizzes are averaged`() {
        val attempts = listOf(completed(10, 80), completed(11, 60))

        assertEquals(70.0, CourseGrades.finalGrade(setOf(10L, 11L), attempts)!!, 0.001)
    }

    @Test
    fun `retakes do not weigh more than other quizzes`() {
        // Antes: (40 + 50 + 60 + 100) / 4 = 62.5. Ahora: mejor de A (60) y B (100) → 80.
        val attempts = listOf(completed(10, 40), completed(10, 50), completed(10, 60), completed(11, 100))

        assertEquals(80.0, CourseGrades.finalGrade(setOf(10L, 11L), attempts)!!, 0.001)
    }

    @Test
    fun `in progress attempts are ignored`() {
        val onlyInProgress = listOf(inProgress(10))
        assertNull(CourseGrades.finalGrade(setOf(10L), onlyInProgress))

        val mixed = listOf(inProgress(10), completed(10, 70))
        assertEquals(70.0, CourseGrades.finalGrade(setOf(10L), mixed)!!, 0.001)
        assertEquals(1, CourseGrades.courseAttempts(setOf(10L), mixed).size)
    }

    @Test
    fun `quiz of the course not taken yet does not lower the grade`() {
        // El curso tiene 2 quizzes y solo hizo uno: se promedia lo que hizo.
        assertEquals(90.0, CourseGrades.finalGrade(setOf(10L, 11L), listOf(completed(10, 90)))!!, 0.001)
    }
}
