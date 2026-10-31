package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.domain.model.TopicStatus
import ApunteIA.FrontEnd.domain.util.Resource
import ApunteIA.FrontEnd.fakes.FakeNoteDataSource.Companion.note
import ApunteIA.FrontEnd.fakes.TestBackend
import ApunteIA.FrontEnd.fakes.attempt
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class MainRepositoryImplTest {

    private fun backendWithData() = TestBackend().apply {
        notes.notes += listOf(
            note(1, title = "JPA", courseId = 1, status = "COMPLETED", summary = "- x"),
            note(2, title = "SQL", courseId = 2, status = "PENDING")
        )
        quizzes.attempts += attempt(1, 50, "COMPLETED", 80.0, title = "Quiz IA - JPA")
        quizzes.attempts += attempt(2, 51, "IN_PROGRESS", null, title = "Quiz IA - SQL")
    }

    @Test
    fun `stats are calculated from notes, topics and completed attempts`() = runBlocking {
        val stats = (MainRepositoryImpl(backendWithData().loader).getUserStats() as Resource.Success).data

        assertEquals("Maya Rodríguez", stats.userName)
        assertEquals(2, stats.notesCount)
        assertEquals(1, stats.quizzesDoneCount)
        // Temas: JPA sólido (80), SQL faltante (0) → 1 pendiente, promedio 40.
        assertEquals(1, stats.pendingTopicsCount)
        assertEquals(40, stats.progressPercent)
    }

    @Test
    fun `topics include one per note plus enrolled courses without notes`() = runBlocking {
        val backend = backendWithData()
        backend.notes.notes.removeAll { it.courseId == 2L }

        val topics = (TopicRepositoryImpl(backend.loader).getTopics() as Resource.Success).data

        assertEquals(2, topics.size)
        assertEquals(TopicStatus.SOLID, topics.first { it.title == "JPA" }.status)
        val missing = topics.first { it.status == TopicStatus.MISSING }
        assertEquals("Bases de datos", missing.className)
    }

    @Test
    fun `recent activity mixes notes and finished quizzes, newest first`() = runBlocking {
        val activity = (MainRepositoryImpl(backendWithData().loader).getRecentActivity() as Resource.Success).data

        assertEquals("Quiz completado · Quiz IA - JPA", activity.first().title)
        assertEquals(3, activity.size)
    }
}