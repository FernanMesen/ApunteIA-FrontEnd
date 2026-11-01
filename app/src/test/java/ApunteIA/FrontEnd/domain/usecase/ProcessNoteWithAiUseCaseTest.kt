package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.model.NoteStatus
import ApunteIA.FrontEnd.domain.repository.NoteRepository
import ApunteIA.FrontEnd.domain.util.Resource
import ApunteIA.FrontEnd.util.AiLanguagePreferenceManager
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

class ProcessNoteWithAiUseCaseTest {

    private class FakeAiLanguagePreferenceManager : AiLanguagePreferenceManager(null) {
        var currentCode = "es"
        override fun getEffectiveLanguageCode(): String = currentCode
    }

    private fun createDummyNote(id: Int) = Note(
        id = id,
        title = "Nota $id",
        className = "Curso",
        classNumber = 1,
        date = "2026-10-31",
        preview = "Preview",
        status = NoteStatus.WEAK
    )

    @Test
    fun `consecutive note generations re-evaluate effective AI language dynamically`() {
        runBlocking {
            val repository = mock(NoteRepository::class.java)
            val fakePrefManager = FakeAiLanguagePreferenceManager()

            `when`(repository.processWithAi(any(), any())).thenAnswer { invocation ->
                val id = invocation.getArgument<Long>(0)
                Resource.Success(createDummyNote(id.toInt()))
            }

            val useCase = ProcessNoteWithAiUseCase(repository, fakePrefManager)

            // 1. PT -> procesar nota A -> language=pt
            fakePrefManager.currentCode = "pt"
            useCase(1L)
            verify(repository).processWithAi(1L, "pt")

            // 2. PT -> procesar nota B -> language=pt
            fakePrefManager.currentCode = "pt"
            useCase(2L)
            verify(repository).processWithAi(2L, "pt")

            // 3. cambiar a EN -> procesar nota C -> language=en
            fakePrefManager.currentCode = "en"
            useCase(3L)
            verify(repository).processWithAi(3L, "en")

            // 4. SAME_AS_APP con UI IT -> procesar nota D -> language=it
            fakePrefManager.currentCode = "it"
            useCase(4L)
            verify(repository).processWithAi(4L, "it")
        }
    }
}
