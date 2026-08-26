package ApunteIA.FrontEnd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import ApunteIA.FrontEnd.domain.model.RecentActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class StudyStats(
    val precision: Int,
    val streak: Int,
    val studyTime: String,
    val weeklyProgress: List<Float>, // Values 0..1 for chart
    val strongAreas: Int,
    val weakAreas: Int
)

class StudyHistoryViewModel : ViewModel() {
    private val _isWeekly = MutableStateFlow(true)
    val isWeekly: StateFlow<Boolean> = _isWeekly.asStateFlow()

    private val _stats = MutableStateFlow(
        StudyStats(
            precision = 53,
            streak = 12,
            studyTime = "4.2h",
            weeklyProgress = listOf(0.4f, 0.7f, 0.3f, 0.9f, 0.6f, 0.2f, 0.5f),
            strongAreas = 4,
            weakAreas = 6
        )
    )
    val stats: StateFlow<StudyStats> = _stats.asStateFlow()

    private val _recentActivity = MutableStateFlow(
        listOf(
            RecentActivity("Escaneadas 3 páginas · Mecanismos de Reacción", "Hace 2h", "Hace 2h"),
            RecentActivity("Cuestionario completado · Estereoquímica (4/5)", "Ayer", "Ayer"),
            RecentActivity("Auditoría actualizada · 2 brechas nuevas", "Hace 2 días", "Hace 2 días")
        )
    )
    val recentActivity: StateFlow<List<RecentActivity>> = _recentActivity.asStateFlow()

    fun setPeriod(isWeekly: Boolean) {
        _isWeekly.value = isWeekly
        // In a real app, we would fetch different data here
    }
}
