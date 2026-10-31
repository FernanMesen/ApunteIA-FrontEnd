package ApunteIA.FrontEnd.presentation.viewmodel

import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.model.ProcessingStatus
import ApunteIA.FrontEnd.domain.model.QuizDifficulty
import ApunteIA.FrontEnd.domain.usecase.CreateNoteUseCase
import ApunteIA.FrontEnd.domain.usecase.DeleteNoteUseCase
import ApunteIA.FrontEnd.domain.usecase.GenerateQuizFromNoteUseCase
import ApunteIA.FrontEnd.domain.usecase.GetNoteDetailUseCase
import ApunteIA.FrontEnd.domain.usecase.ProcessNoteWithAiUseCase
import ApunteIA.FrontEnd.domain.util.Resource
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NoteDetailUiState(
    val isLoading: Boolean = true,
    val note: Note? = null,
    val error: String? = null,
    /** La IA está resumiendo la nota. */
    val isSummarizing: Boolean = false,
    /** La IA está generando un quiz. */
    val isGeneratingQuiz: Boolean = false,
    val isDeleting: Boolean = false,
    val actionError: String? = null,
    /** Id de la evaluación recién generada: la pantalla navega al quiz y lo consume. */
    val generatedQuizId: Long? = null,
    val deleted: Boolean = false
) {
    val isBusy: Boolean get() = isSummarizing || isGeneratingQuiz || isDeleting
}

/**
 * Detalle de una nota con las dos integraciones de IA del backend:
 *  - Resumir con IA → POST /notes/{id}/process
 *  - Generar quiz con IA → POST /notes/{id}/assessments/generate
 */
@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getNoteDetail: GetNoteDetailUseCase,
    private val processNoteWithAi: ProcessNoteWithAiUseCase,
    private val generateQuizFromNote: GenerateQuizFromNoteUseCase,
    private val createNote: CreateNoteUseCase,
    private val deleteNote: DeleteNoteUseCase
) : ViewModel() {

    private var noteId: Long = savedStateHandle.get<Long>(NOTE_ID_ARG) ?: 0L

    private val _uiState = MutableStateFlow(NoteDetailUiState())
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = getNoteDetail(noteId)) {
                is Resource.Success -> _uiState.update { it.copy(isLoading = false, note = result.data) }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    /**
     * Resume la nota con IA. El backend solo procesa notas PENDING; si un intento
     * anterior falló (FAILED), se crea una copia de la nota y se procesa esa.
     */
    fun summarize() {
        val note = _uiState.value.note ?: return
        if (_uiState.value.isBusy) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSummarizing = true, actionError = null) }

            var targetId = note.id.toLong()
            if (note.processingStatus == ProcessingStatus.FAILED) {
                when (val copy = createNote(note.title, note.content, note.courseId)) {
                    is Resource.Success -> {
                        deleteNote(targetId)
                        targetId = copy.data.id.toLong()
                        noteId = targetId
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isSummarizing = false, actionError = copy.message) }
                        return@launch
                    }
                    is Resource.Loading -> Unit
                }
            }

            when (val result = processNoteWithAi(targetId)) {
                is Resource.Success -> _uiState.update {
                    it.copy(
                        isSummarizing = false,
                        note = result.data.copy(classNumber = note.classNumber, status = note.status)
                    )
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isSummarizing = false, actionError = result.message) }
                    reloadQuietly()
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun generateQuiz(questionCount: Int = 5, difficulty: QuizDifficulty = QuizDifficulty.MEDIUM) {
        val note = _uiState.value.note ?: return
        if (_uiState.value.isBusy) return
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingQuiz = true, actionError = null) }
            when (val result = generateQuizFromNote(note.id.toLong(), questionCount, difficulty)) {
                is Resource.Success -> _uiState.update {
                    it.copy(isGeneratingQuiz = false, generatedQuizId = result.data)
                }
                is Resource.Error -> _uiState.update {
                    it.copy(isGeneratingQuiz = false, actionError = result.message)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun onQuizOpened() {
        _uiState.update { it.copy(generatedQuizId = null) }
    }

    fun delete() {
        val note = _uiState.value.note ?: return
        if (_uiState.value.isBusy) return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, actionError = null) }
            when (val result = deleteNote(note.id.toLong())) {
                is Resource.Success -> _uiState.update { it.copy(isDeleting = false, deleted = true) }
                is Resource.Error -> _uiState.update { it.copy(isDeleting = false, actionError = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun dismissActionError() {
        _uiState.update { it.copy(actionError = null) }
    }

    private suspend fun reloadQuietly() {
        val result = getNoteDetail(noteId)
        if (result is Resource.Success) _uiState.update { it.copy(note = result.data) }
    }

    companion object {
        const val NOTE_ID_ARG = "noteId"
    }
}