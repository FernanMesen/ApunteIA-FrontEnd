package ApunteIA.FrontEnd.presentation.viewmodel

import ApunteIA.FrontEnd.domain.model.AuditSummary
import ApunteIA.FrontEnd.domain.model.Topic
import ApunteIA.FrontEnd.domain.usecase.CalculateAuditSummaryUseCase
import ApunteIA.FrontEnd.domain.usecase.FilterTopicsUseCase
import ApunteIA.FrontEnd.domain.usecase.GetTopicsUseCase
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

@HiltViewModel
class AuditViewModel @Inject constructor(
    private val getTopicsUseCase: GetTopicsUseCase,
    private val filterTopicsUseCase: FilterTopicsUseCase,
    private val calculateAuditSummaryUseCase: CalculateAuditSummaryUseCase
) : ViewModel() {

    private val _topics = MutableStateFlow<List<Topic>>(emptyList())
    val topics: StateFlow<List<Topic>> = _topics.asStateFlow()

    private val _selectedCategory = MutableStateFlow(FilterTopicsUseCase.CATEGORY_ALL)
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val categories: StateFlow<List<String>> = _topics
        .map { topics -> listOf(FilterTopicsUseCase.CATEGORY_ALL) + topics.map { it.className }.distinct() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), listOf(FilterTopicsUseCase.CATEGORY_ALL))

    val summary: StateFlow<AuditSummary> = _topics
        .map { calculateAuditSummaryUseCase(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AuditSummary(0, 0, 0, 0))

    val filteredTopics: StateFlow<List<Topic>> = combine(
        _topics,
        _selectedCategory
    ) { topics, category ->
        filterTopicsUseCase(topics, category)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init { loadTopics() }

    private fun loadTopics() {
        viewModelScope.launch {
            getTopicsUseCase()
                .onSuccess { topics -> _topics.value = topics }
                .onFailure { _topics.value = emptyList() }
        }
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }
}