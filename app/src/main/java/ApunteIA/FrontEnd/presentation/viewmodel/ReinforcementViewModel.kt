package ApunteIA.FrontEnd.presentation.viewmodel

import ApunteIA.FrontEnd.domain.model.Assessment
import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.model.NoteStatus
import ApunteIA.FrontEnd.domain.model.QuizDifficulty
import ApunteIA.FrontEnd.domain.usecase.GenerateQuizFromNoteUseCase
import ApunteIA.FrontEnd.domain.usecase.GetAvailableQuizzesUseCase
import ApunteIA.FrontEnd.domain.usecase.GetNotesUseCase
import ApunteIA.FrontEnd.domain.util.Resource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReinforcementUiState(
    val isLoading: Boolean = false,
    val notes: List<Note> = emptyList(),
    val quizzes: List<Assessment> = emptyList(),
    val selectedNoteId: Int? = null,
    val questionCount: Int = 5,
    val difficulty: QuizDifficulty = QuizDifficulty.MEDIUM,
    val onlyWeak: Boolean = false,
    val isGenerating: Boolean = false,
    val error: String? = null,
    /** Id de la evaluación a abrir; la pantalla navega y lo consume. */
    val quizToOpen: Long? = null
) {
    val visibleNotes: List<Note>
        get() = if (onlyWeak) notes.filter { it.status == NoteStatus.WEAK } else notes

    val canGenerate: Boolean
        get() = !isGenerating && selectedNoteId != null && visibleNotes.any { it.id == selectedNoteId }
}

/**
 * Pantalla "Reforzar": el usuario elige una nota, cantidad y dificultad, y la IA del
 * backend genera el quiz. También lista los quizzes ya existentes de sus cursos.
 */
@HiltViewModel
class ReinforcementViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val getAvailableQuizzesUseCase: GetAvailableQuizzesUseCase,
    private val generateQuizFromNote: GenerateQuizFromNoteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReinforcementUiState())
    val uiState: StateFlow<ReinforcementUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val quizzesDeferred = async { getAvailableQuizzesUseCase() }
            when (val notes = getNotesUseCase()) {
                is Resource.Success -> {
                    val quizzes = (quizzesDeferred.await() as? Resource.Success)?.data.orEmpty()
                    _uiState.update { state ->
                        val stillSelected = notes.data.any { it.id == state.selectedNoteId }
                        state.copy(
                            isLoading = false,
                            notes = notes.data,
                            quizzes = quizzes,
                            selectedNoteId = if (stillSelected) state.selectedNoteId else notes.data.firstOrNull()?.id
                        )
                    }
                }
                is Resource.Error -> {
                    quizzesDeferred.cancel()
                    _uiState.update { it.copy(isLoading = false, error = notes.message) }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun selectNote(id: Int) = _uiState.update { it.copy(selectedNoteId = id) }

    fun selectQuestionCount(count: Int) = _uiState.update { it.copy(questionCount = count) }

    fun selectDifficulty(difficulty: QuizDifficulty) = _uiState.update { it.copy(difficulty = difficulty) }

    fun toggleOnlyWeak() = _uiState.update { state ->
        val onlyWeak = !state.onlyWeak
        val visible = if (onlyWeak) state.notes.filter { it.status == NoteStatus.WEAK } else state.notes
        state.copy(
            onlyWeak = onlyWeak,
            selectedNoteId = state.selectedNoteId?.takeIf { id -> visible.any { it.id == id } }
                ?: visible.firstOrNull()?.id
        )
    }

    fun generate() {
        val state = _uiState.value
        val noteId = state.selectedNoteId ?: return
        if (!state.canGenerate) return
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, error = null) }
            when (val result = generateQuizFromNote(noteId.toLong(), state.questionCount, state.difficulty)) {
                is Resource.Success -> _uiState.update { it.copy(isGenerating = false, quizToOpen = result.data) }
                is Resource.Error -> _uiState.update { it.copy(isGenerating = false, error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun openQuiz(assessmentId: Long) = _uiState.update { it.copy(quizToOpen = assessmentId) }

    fun onQuizOpened() = _uiState.update { it.copy(quizToOpen = null) }

    fun dismissError() = _uiState.update { it.copy(error = null) }
}