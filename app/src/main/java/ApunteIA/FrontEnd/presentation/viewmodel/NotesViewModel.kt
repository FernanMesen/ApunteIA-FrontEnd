package ApunteIA.FrontEnd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.error.NoteError
import ApunteIA.FrontEnd.domain.usecase.FilterNotesUseCase
import ApunteIA.FrontEnd.domain.usecase.GetNotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotesUiState(
    val isLoading: Boolean = false,
    val notes: List<Note> = emptyList(),
    val error: NoteError? = null
)

/**
 * Maneja el estado de la pantalla de Notas.
 */
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val filterNotesUseCase: FilterNotesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(FilterNotesUseCase.CATEGORY_ALL)
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val categories: StateFlow<List<String>> = _uiState
        .map { state -> 
            listOf<String>(FilterNotesUseCase.CATEGORY_ALL) + state.notes.map { it.className }.distinct() 
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = listOf<String>(FilterNotesUseCase.CATEGORY_ALL)
        )

    val filteredNotes: StateFlow<List<Note>> = combine(
        _uiState,
        _searchQuery,
        _selectedCategory
    ) { state, query, category ->
        filterNotesUseCase(state.notes, query, category)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    init {
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getNotesUseCase()
                .onSuccess { notes -> 
                    _uiState.value = _uiState.value.copy(isLoading = false, notes = notes, error = null)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, 
                        error = error as? NoteError ?: NoteError.Unknown
                    )
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    fun noteByTitle(title: String): Note? =
        _uiState.value.notes.firstOrNull { it.title == title }
}
