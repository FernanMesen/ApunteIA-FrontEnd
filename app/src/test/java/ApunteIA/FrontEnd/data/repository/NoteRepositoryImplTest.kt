package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.domain.model.NoteStatus
import ApunteIA.FrontEnd.domain.model.ProcessingStatus
import ApunteIA.FrontEnd.domain.model.QuizDifficulty
import ApunteIA.FrontEnd.domain.util.Resource
import ApunteIA.FrontEnd.fakes.FakeNoteDataSource.Companion.note
import ApunteIA.FrontEnd.fakes.FakeSessionStore
import ApunteIA.FrontEnd.fakes.TestBackend
import ApunteIA.FrontEnd.fakes.attempt
import ApunteIA.FrontEnd.fakes.httpError
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteRepositoryImplTest {

    private fun repository(backend: TestBackend) =
        NoteRepositoryImpl(backend.notes, backend.loader, backend.catalog)

    @Test
    fun `getNotes returns only my notes with course name, class number and newest first`() = runBlocking {
        val backend = TestBackend()
        backend.notes.notes += listOf(
            note(1, title = "Intro", courseId = 1, createdAt = "2026-10-01T10:00:00"),
            note(2, title = "JPA", courseId = 1, createdAt = "2026-10-03T10:00:00"),
            note(3, title = "SQL", courseId = 2, createdAt = "2026-10-02T10:00:00"),
            note(4, title = "De otro usuario", userId = 99)
        )

        val result = repository(backend).getNotes() as Resource.Success
        val notes = result.data

        assertEquals(listOf("JPA", "SQL", "Intro"), notes.map { it.title })
        val jpa = notes.first { it.title == "JPA" }
        assertEquals("Backend para plataformas móviles", jpa.className)
        assertEquals(2, jpa.classNumber)
        assertEquals("Bases de datos", notes.first { it.title == "SQL" }.className)
    }

    @Test
    fun `note mastery is solid when an AI quiz of the note scored 70 or more`() = runBlocking {
        val backend = TestBackend()
        backend.notes.notes += listOf(note(1, title = "JPA"), note(2, title = "SQL"))
        backend.quizzes.attempts += attempt(1, 50, "COMPLETED", 80.0, title = "Quiz IA - JPA")
        backend.quizzes.attempts += attempt(2, 51, "COMPLETED", 40.0, title = "Quiz IA - SQL (2)")

        val notes = (repository(backend).getNotes() as Resource.Success).data

        assertEquals(NoteStatus.SOLID, notes.first { it.title == "JPA" }.status)
        assertEquals(NoteStatus.WEAK, notes.first { it.title == "SQL" }.status)
    }

    @Test
    fun `createNote sends the logged in user id`() = runBlocking {
        val backend = TestBackend(session = FakeSessionStore.loggedIn(userId = 2))

        val result = repository(backend).createNote("Clase 5", "texto OCR", 1)

        assertTrue(result is Resource.Success)
        assertEquals(2L, backend.notes.lastCreate?.userId)
        assertEquals(1L, backend.notes.lastCreate?.courseId)
        assertEquals(null, backend.notes.lastCreate?.noteDate)
    }

    @Test
    fun `createNote sends the note date`() = runBlocking {
        val backend = TestBackend(session = FakeSessionStore.loggedIn(userId = 2))

        val result = repository(backend).createNote("Clase 5", "texto", 1, "2026-10-20")

        assertTrue(result is Resource.Success)
        assertEquals("2026-10-20", backend.notes.lastCreate?.noteDate)
    }

    @Test
    fun `processWithAi returns the summary`() = runBlocking {
        val backend = TestBackend()
        backend.notes.notes += note(1)

        val note = (repository(backend).processWithAi(1, "es") as Resource.Success).data

        assertEquals(ProcessingStatus.COMPLETED, note.processingStatus)
        assertEquals("- Punto clave (es)", note.summary)
    }

    @Test
    fun `processWithAi sends correct language parameter`() = runBlocking {
        val backend = TestBackend()
        backend.notes.notes += note(1)

        repository(backend).processWithAi(1, "en")

        assertEquals("en", backend.notes.lastProcessedLanguage)
    }

    @Test
    fun `processWithAi shows the backend message when the AI is not configured`() = runBlocking {
        val backend = TestBackend()
        backend.notes.notes += note(1)
        backend.notes.processError = httpError(503, "La IA no está configurada (falta AI_API_KEY)")

        val result = repository(backend).processWithAi(1, "es")

        assertEquals("La IA no está configurada (falta AI_API_KEY)", (result as Resource.Error).message)
    }

    @Test
    fun `generateQuiz returns the new assessment id`() = runBlocking {
        val backend = TestBackend()
        backend.notes.notes += note(1, title = "JPA")

        val result = repository(backend).generateQuiz(1, 5, QuizDifficulty.HARD, "en")

        assertEquals(50L, (result as Resource.Success).data)
    }

    @Test
    fun `generateQuiz sends correct language parameter`() = runBlocking {
        val backend = TestBackend()
        backend.notes.notes += note(1, title = "JPA")

        repository(backend).generateQuiz(1, 5, QuizDifficulty.HARD, "pt")

        assertEquals("pt", backend.notes.lastAssessmentLanguage)
    }

    @Test
    fun `without session getNotes asks to log in`() = runBlocking {
        val backend = TestBackend(session = FakeSessionStore())

        val result = repository(backend).getNotes()

        assertEquals("Inicia sesión para ver tus notas.", (result as Resource.Error).message)
    }
}
