package ApunteIA.FrontEnd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ApunteIA.FrontEnd.domain.model.RecentActivity
import ApunteIA.FrontEnd.domain.model.UserStats
import ApunteIA.FrontEnd.domain.usecase.GetMainDashboardUseCase
import ApunteIA.FrontEnd.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val isLoading: Boolean = false,
    val userStats: UserStats? = null,
    val recentActivity: List<RecentActivity> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getMainDashboardUseCase: GetMainDashboardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    // Backwards compatibility for callers observing individual flows
    val userStats: StateFlow<UserStats?> get() = MutableStateFlow(_uiState.value.userStats).asStateFlow()
    val recentActivity: StateFlow<List<RecentActivity>> get() = MutableStateFlow(_uiState.value.recentActivity).asStateFlow()



    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            var stats: UserStats? = _uiState.value.userStats
            var activity: List<RecentActivity> = _uiState.value.recentActivity
            var errorMessage: String? = null

            when (val statsResult = getMainDashboardUseCase.getUserStats()) {
                is Resource.Success -> {
                    stats = statsResult.data
                }
                is Resource.Error -> {
                    errorMessage = statsResult.message
                }
                is Resource.Loading -> { }
            }

            when (val activityResult = getMainDashboardUseCase.getRecentActivity()) {
                is Resource.Success -> {
                    activity = activityResult.data
                }
                is Resource.Error -> {
                    if (errorMessage == null) {
                        errorMessage = activityResult.message
                    }
                }
                is Resource.Loading -> { }
            }

            _uiState.value = MainUiState(
                isLoading = false,
                userStats = stats,
                recentActivity = activity,
                error = errorMessage
            )
        }
    }

    fun refresh() {
        loadDashboard()
    }
}
