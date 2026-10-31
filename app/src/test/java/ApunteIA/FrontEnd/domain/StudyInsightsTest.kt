package ApunteIA.FrontEnd.domain

import ApunteIA.FrontEnd.domain.model.AssessmentAttempt
import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.model.NoteStatus
import ApunteIA.FrontEnd.domain.model.ProcessingStatus
import ApunteIA.FrontEnd.domain.model.TopicStatus
import ApunteIA.FrontEnd.domain.usecase.StudyInsights
import ApunteIA.FrontEnd.domain.util.IsoDates
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StudyInsightsTest {

    private val dayMs = 24L * 60 * 60 * 1000

    private fun note(id: Int, title: String, status: ProcessingStatus = ProcessingStatus.PENDING) = Note(
        id = id, title = title, className = "Curso", classNumber = 1, date = "", preview = "",
        status = NoteStatus.WEAK, courseId = 1, processingStatus = status
    )

    private fun attempt(title: String, score: Int, completed: Boolean = true) = AssessmentAttempt(
        id = 1, assessmentId = "1", assessmentTitle = title, scorePercent = score,
        completedAt = if (completed) "2026-10-05T12:00:00Z" else ""
    )

    @Test
    fun `AI quiz titles are matched to their note, including repeated ones`() {
        assertTrue(StudyInsights.quizBelongsToNote("Quiz IA - JPA", "JPA"))
        assertTrue(StudyInsights.quizBelongsToNote("Quiz IA - JPA (3)", "JPA"))
        assertFalse(StudyInsights.quizBelongsToNote("Quiz IA - JPA avanzado", "JPA"))
        assertFalse(StudyInsights.quizBelongsToNote("Parcial", "JPA"))
    }

    @Test
    fun `topic status follows the best quiz score and the AI summary`() {
        val notes = listOf(
            note(1, "A"),
            note(2, "B"),
            note(3, "C", ProcessingStatus.COMPLETED),
            note(4, "D")
        )
        val attempts = listOf(
            attempt("Quiz IA - A", 40), attempt("Quiz IA - A (2)", 90),
            attempt("Quiz IA - B", 50),
            attempt("Quiz IA - D", 100, completed = false)
        )

        val topics = StudyInsights.buildTopics(notes, emptyList(), attempts).associateBy { it.title }

        assertEquals(TopicStatus.SOLID, topics.getValue("A").status)
        assertEquals(90, topics.getValue("A").progress)
        assertEquals(TopicStatus.WEAK, topics.getValue("B").status)
        assertEquals(TopicStatus.WEAK, topics.getValue("C").status)
        assertEquals(StudyInsights.SUMMARIZED_PROGRESS, topics.getValue("C").progress)
        assertEquals(TopicStatus.MISSING, topics.getValue("D").status)
    }

    @Test
    fun `streak counts consecutive days up to today or yesterday`() {
        val now = IsoDates.startOfDay(System.currentTimeMillis()) + 12 * 60 * 60 * 1000
        assertEquals(3, StudyInsights.streakDays(listOf(now, now - dayMs, now - 2 * dayMs, now - 5 * dayMs), now))
        assertEquals(2, StudyInsights.streakDays(listOf(now - dayMs, now - 2 * dayMs), now))
        assertEquals(0, StudyInsights.streakDays(listOf(now - 3 * dayMs), now))
        assertEquals(0, StudyInsights.streakDays(emptyList(), now))
    }

    @Test
    fun `iso dates from the backend are parsed`() {
        assertNotNull(IsoDates.parse("2026-10-29T17:18:01.123456"))
        assertNotNull(IsoDates.parse("2026-10-29T23:18:01Z"))
        assertNotNull(IsoDates.parse("2026-10-29"))
        assertNull(IsoDates.parse("hoy"))
        assertNull(IsoDates.parse(null))
        assertEquals(
            IsoDates.parse("2026-10-29T17:18:01.999"),
            IsoDates.parse("2026-10-29T17:18:01Z")
        )
    }

    @Test
    fun `relative time is human friendly`() {
        val now = IsoDates.parse("2026-10-29T12:00:00Z")!!
        assertEquals("Justo ahora", IsoDates.relative("2026-10-29T11:59:40Z", now))
        assertEquals("Hace 15 min", IsoDates.relative("2026-10-29T11:45:00Z", now))
    }
}