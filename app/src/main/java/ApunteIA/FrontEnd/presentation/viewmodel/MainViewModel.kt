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

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getMainDashboardUseCase: GetMainDashboardUseCase
) : ViewModel() {

    private val _userStats = MutableStateFlow<UserStats?>(null)
    val userStats: StateFlow<UserStats?> = _userStats.asStateFlow()

    private val _recentActivity = MutableStateFlow<List<RecentActivity>>(emptyList())
    val recentActivity: StateFlow<List<RecentActivity>> = _recentActivity.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            val statsResult = getMainDashboardUseCase.getUserStats()
            if (statsResult is Resource.Success) {
                _userStats.value = statsResult.data
            }
            
            val activityResult = getMainDashboardUseCase.getRecentActivity()
            if (activityResult is Resource.Success) {
                _recentActivity.value = activityResult.data
            }
        }
    }
}
