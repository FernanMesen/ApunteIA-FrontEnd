package ApunteIA.FrontEnd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.usecase.FilterNotesUseCase
import ApunteIA.FrontEnd.domain.usecase.GetNotesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Maneja el estado de la pantalla de Notas. A diferencia de la versión
 * anterior (MVVM simple), ya no conoce de dónde salen los datos: solo habla
 * con los casos de uso del dominio ([GetNotesUseCase], [FilterNotesUseCase]),
 * que a su vez dependen de la interfaz [ApunteIA.FrontEnd.domain.repository.NoteRepository].
 * Quién implementa esa interfaz (datos locales de ejemplo hoy, una API mañana)
 * es un detalle de la capa `data` que este ViewModel no necesita conocer.
 */
class NotesViewModel(
    private val getNotesUseCase: GetNotesUseCase,
    private val filterNotesUseCase: FilterNotesUseCase
) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(FilterNotesUseCase.CATEGORY_ALL)
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val categories: StateFlow<List<String>> = _notes
        .map { notes -> listOf(FilterNotesUseCase.CATEGORY_ALL) + notes.map { it.className }.distinct() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = listOf(FilterNotesUseCase.CATEGORY_ALL)
        )

    val filteredNotes: StateFlow<List<Note>> = combine(
        _notes,
        _searchQuery,
        _selectedCategory
    ) { notes, query, category ->
        filterNotesUseCase(notes, query, category)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            getNotesUseCase()
                .onSuccess { notes -> _notes.value = notes }
                .onFailure {
                    // Sin backend real todavía no hay UI de error; se deja
                    // la lista vacía. Cuando exista, este es el lugar para
                    // exponer un estado de error a la pantalla.
                    _notes.value = emptyList()
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
        _notes.value.firstOrNull { it.title == title }
}
