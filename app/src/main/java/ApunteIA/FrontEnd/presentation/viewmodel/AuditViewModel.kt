package ApunteIA.FrontEnd.presentation.viewmodel

import ApunteIA.FrontEnd.domain.model.AuditSummary
import ApunteIA.FrontEnd.domain.model.Topic
import ApunteIA.FrontEnd.domain.usecase.CalculateAuditSummaryUseCase
import ApunteIA.FrontEnd.domain.usecase.FilterTopicsUseCase
import ApunteIA.FrontEnd.domain.usecase.GetTopicsUseCase
import ApunteIA.FrontEnd.domain.util.Resource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class AuditUiState(
    val isLoading: Boolean = false,
    val topics: List<Topic> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class AuditViewModel @Inject constructor(
    private val getTopicsUseCase: GetTopicsUseCase,
    private val filterTopicsUseCase: FilterTopicsUseCase,
    private val calculateAuditSummaryUseCase: CalculateAuditSummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuditUiState())
    val uiState: StateFlow<AuditUiState> = _uiState.asStateFlow()

    private val _selectedCategory = MutableStateFlow(FilterTopicsUseCase.CATEGORY_ALL)
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val categories: StateFlow<List<String>> = _uiState
        .map { state -> listOf(FilterTopicsUseCase.CATEGORY_ALL) + state.topics.map { it.className }.distinct() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), listOf(FilterTopicsUseCase.CATEGORY_ALL))

    val summary: StateFlow<AuditSummary> = _uiState
        .map { calculateAuditSummaryUseCase(it.topics) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AuditSummary(0, 0, 0, 0))

    val filteredTopics: StateFlow<List<Topic>> = combine(
        _uiState,
        _selectedCategory
    ) { state, category ->
        filterTopicsUseCase(state.topics, category)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Backwards compatibility for callers observing topic flow
    val topics: StateFlow<List<Topic>> get() = MutableStateFlow(_uiState.value.topics).asStateFlow()

    init { loadTopics() }

    fun loadTopics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = getTopicsUseCase()) {
                is Resource.Success -> {
                    _uiState.value = AuditUiState(
                        isLoading = false,
                        topics = result.data,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun refresh() {
        loadTopics()
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }
}
