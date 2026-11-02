package ApunteIA.FrontEnd.presentation.viewmodel

import ApunteIA.FrontEnd.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ApunteIA.FrontEnd.domain.model.RecentActivity
import ApunteIA.FrontEnd.domain.model.StudyOverview
import ApunteIA.FrontEnd.domain.model.TopicStatus
import ApunteIA.FrontEnd.domain.usecase.GetMainDashboardUseCase
import ApunteIA.FrontEnd.domain.usecase.StudyInsights
import ApunteIA.FrontEnd.domain.util.IsoDates
import ApunteIA.FrontEnd.domain.util.Resource
import ApunteIA.FrontEnd.util.L10n
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class StudyStats(
    val precision: Int,
    val streak: Int,
    val studyTime: String,
    /** 7 valores 0..1 (lunes a domingo de la semana actual). */
    val weeklyProgress: List<Float>,
    val strongAreas: Int,
    val weakAreas: Int
)

data class SubjectProgress(
    val name: String,
    val progress: Int,
    val colorHex: String
)

data class StudyHistoryUiState(
    val isLoading: Boolean = false,
    val isWeekly: Boolean = true,
    val stats: StudyStats = StudyStats(
        precision = 0,
        streak = 0,
        studyTime = L10n.get(R.string.study_time_minutes, 0),
        weeklyProgress = List(7) { 0f },
        strongAreas = 0,
        weakAreas = 0
    ),
    val subjects: List<SubjectProgress> = emptyList(),
    val recentActivity: List<RecentActivity> = emptyList(),
    val error: String? = null
)

/** Historial calculado con los intentos de quiz y las notas reales del usuario. */
@HiltViewModel
class StudyHistoryViewModel @Inject constructor(
    private val getMainDashboardUseCase: GetMainDashboardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudyHistoryUiState())
    val uiState: StateFlow<StudyHistoryUiState> = _uiState.asStateFlow()

    private var overview: StudyOverview? = null

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = getMainDashboardUseCase.getOverview()) {
                is Resource.Success -> {
                    overview = result.data
                    applyPeriod()
                }
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                is Resource.Loading -> Unit
            }
        }
    }

    fun refresh() {
        loadHistory()
    }

    fun setPeriod(isWeekly: Boolean) {
        _uiState.value = _uiState.value.copy(isWeekly = isWeekly)
        if (overview != null) applyPeriod() else loadHistory()
    }

    private fun applyPeriod() {
        val data = overview ?: return
        val now = System.currentTimeMillis()
        val periodDays = if (_uiState.value.isWeekly) 7 else 30
        val since = now - periodDays * DAY_MS

        val completed = data.attempts.filter { StudyInsights.isCompleted(it) }
        val inPeriod = completed.filter { (IsoDates.parse(it.completedAt) ?: 0L) >= since }
        val precision = if (inPeriod.isEmpty()) 0 else inPeriod.sumOf { it.scorePercent } / inPeriod.size
        val seconds = inPeriod.sumOf { it.durationSeconds }

        // Actividad (notas + quizzes) de lunes a domingo de esta semana.
        val weekStart = IsoDates.startOfWeek(now)
        val counts = IntArray(7)
        StudyInsights.activityMillis(data.notes, data.attempts).forEach { millis ->
            val index = ((millis - weekStart) / DAY_MS).toInt()
            if (index in 0..6) counts[index]++
        }
        val max = counts.maxOrNull()?.takeIf { it > 0 } ?: 1
        val weekly = counts.map { it.toFloat() / max }

        val subjects = data.myCourses.map { course ->
            val topics = data.topics.filter { it.className == course.name }
            SubjectProgress(
                name = course.name,
                progress = if (topics.isEmpty()) 0 else topics.sumOf { it.progress } / topics.size,
                colorHex = courseColor(course.id)
            )
        }

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = null,
            stats = StudyStats(
                precision = precision,
                streak = StudyInsights.streakDays(StudyInsights.activityMillis(data.notes, data.attempts), now),
                studyTime = formatDuration(seconds),
                weeklyProgress = weekly,
                strongAreas = data.topics.count { it.status == TopicStatus.SOLID },
                weakAreas = data.topics.count { it.status != TopicStatus.SOLID }
            ),
            subjects = subjects,
            recentActivity = StudyInsights.recentActivity(data.notes, data.attempts, limit = 8, now = now)
        )
    }

    private fun formatDuration(seconds: Long): String = when {
        seconds < 3600 -> L10n.get(R.string.study_time_minutes, seconds / 60)
        else -> String.format(Locale.US, "%.1fh", seconds / 3600.0)
    }

    private companion object {
        const val DAY_MS = 24L * 60 * 60 * 1000
    }
}
