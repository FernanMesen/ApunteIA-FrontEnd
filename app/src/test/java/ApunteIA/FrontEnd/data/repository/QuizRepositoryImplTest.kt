package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.domain.util.Resource
import ApunteIA.FrontEnd.fakes.TestBackend
import ApunteIA.FrontEnd.fakes.assessment
import ApunteIA.FrontEnd.fakes.attempt
import ApunteIA.FrontEnd.fakes.httpError
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class QuizRepositoryImplTest {

    private fun repository(backend: TestBackend) = QuizRepositoryImpl(backend.quizzes, backend.loader)

    @Test
    fun `startQuiz loads the questions without the correct answer and opens an attempt`() = runBlocking {
        val backend = TestBackend()
        backend.quizzes.assessments[50] = assessment(50, "Quiz IA - JPA", questions = 3)

        val session = (repository(backend).startQuiz(50) as Resource.Success).data

        assertEquals(7L, session.attemptId)
        assertEquals(3, session.questions.size)
        assertTrue(session.questions.all { it.correctAnswerIndex == -1 })
    }

    @Test
    fun `startQuiz reuses the attempt in progress when the backend answers 409`() = runBlocking {
        val backend = TestBackend()
        backend.quizzes.assessments[50] = assessment(50, "Quiz IA - JPA")
        backend.quizzes.startError = httpError(409, "Ya tiene un intento en progreso")
        backend.quizzes.attempts += attempt(id = 33, assessmentId = 50, status = "IN_PROGRESS", score = null)

        val session = (repository(backend).startQuiz(50) as Resource.Success).data

        assertEquals(33L, session.attemptId)
    }

    @Test
    fun `submitQuiz maps score and correct options`() = runBlocking {
        val backend = TestBackend()

        val result = (repository(backend).submitQuiz(7, listOf(0, 2)) as Resource.Success).data

        assertEquals(listOf(0, 2), backend.quizzes.submitted)
        assertEquals(50, result.scorePercent)
        assertEquals(listOf(true, false), result.answers.map { it.isCorrect })
        assertEquals(listOf(0, 0), result.answers.map { it.correctOptionIndex })
    }

    @Test
    fun `available quizzes come from my courses and are marked when completed`() = runBlocking {
        val backend = TestBackend()
        backend.quizzes.courseAssessments[1] = listOf(assessment(50, "Quiz IA - JPA"), assessment(51, "Parcial"))
        backend.quizzes.courseAssessments[2] = listOf(assessment(60, "Quiz SQL", courseId = 2))
        backend.quizzes.courseAssessments[3] = listOf(assessment(70, "No inscrito", courseId = 3))
        backend.quizzes.attempts += attempt(1, 51, "COMPLETED", 90.0)

        val quizzes = (repository(backend).getAvailableQuizzes() as Resource.Success).data

        assertEquals(listOf(60, 51, 50), quizzes.map { it.id })
        assertTrue(quizzes.first { it.id == 51 }.isCompleted)
    }

    @Test
    fun `network error returns a friendly message`() = runBlocking {
        val backend = TestBackend()
        backend.quizzes.submitResult = { throw IOException() }

        val result = repository(backend).submitQuiz(7, listOf(0))

        assertEquals("No hay conexión para cargar el cuestionario.", (result as Resource.Error).message)
    }
}