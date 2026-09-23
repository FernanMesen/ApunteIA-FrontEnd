package ApunteIA.FrontEnd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ApunteIA.FrontEnd.domain.model.RecentActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StudyStats(
    val precision: Int,
    val streak: Int,
    val studyTime: String,
    val weeklyProgress: List<Float>,
    val strongAreas: Int,
    val weakAreas: Int
)

data class StudyHistoryUiState(
    val isLoading: Boolean = false,
    val isWeekly: Boolean = true,
    val stats: StudyStats = StudyStats(
        precision = 53,
        streak = 12,
        studyTime = "4.2h",
        weeklyProgress = listOf(0.4f, 0.7f, 0.3f, 0.9f, 0.6f, 0.2f, 0.5f),
        strongAreas = 4,
        weakAreas = 6
    ),
    val recentActivity: List<RecentActivity> = listOf(
        RecentActivity("Escaneadas 3 páginas · Mecanismos de Reacción", "Hace 2h", "Hace 2h"),
        RecentActivity("Cuestionario completado · Estereoquímica (4/5)", "Ayer", "Ayer"),
        RecentActivity("Auditoría actualizada · 2 brechas nuevas", "Hace 2 días", "Hace 2 días")
    ),
    val error: String? = null
)

@HiltViewModel
class StudyHistoryViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(StudyHistoryUiState())
    val uiState: StateFlow<StudyHistoryUiState> = _uiState.asStateFlow()

    // Backwards compatibility for callers observing individual flows
    val isWeekly: StateFlow<Boolean> get() = MutableStateFlow(_uiState.value.isWeekly).asStateFlow()
    val stats: StateFlow<StudyStats> get() = MutableStateFlow(_uiState.value.stats).asStateFlow()
    val recentActivity: StateFlow<List<RecentActivity>> get() = MutableStateFlow(_uiState.value.recentActivity).asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            delay(300) // Simular latencia de red al cargar
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = null
            )
        }
    }

    fun refresh() {
        loadHistory()
    }

    fun setPeriod(isWeekly: Boolean) {
        _uiState.value = _uiState.value.copy(isWeekly = isWeekly)
        loadHistory()
    }
}
