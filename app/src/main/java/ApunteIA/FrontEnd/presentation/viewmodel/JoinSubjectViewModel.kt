package ApunteIA.FrontEnd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ApunteIA.FrontEnd.domain.usecase.AddTopicUseCase
import ApunteIA.FrontEnd.domain.usecase.GetTopicsUseCase
import ApunteIA.FrontEnd.domain.util.Resource
import ApunteIA.FrontEnd.presentation.ui.screens.AvailableCourse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JoinSubjectUiState(
    val isLoading: Boolean = false,
    val availableCourses: List<AvailableCourse> = emptyList(),
    val isJoining: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class JoinSubjectViewModel @Inject constructor(
    private val getTopicsUseCase: GetTopicsUseCase,
    private val addTopicUseCase: AddTopicUseCase
) : ViewModel() {

    private val allCourses = listOf(
        AvailableCourse("Álgebra Lineal", "Dr. Nora Belén", "Fall 2026", "LIN-1180", "#2E3B3B"),
        AvailableCourse("Introducción a la Psicología", "Prof. Iván Duarte", "Fall 2026", "PSY-1010", "#8FB2C9"),
        AvailableCourse("Química Orgánica", "Dr. Alan Reyes", "Fall 2026", "QO-2020", "#0A3326"),
        AvailableCourse("Biología Celular", "Dr. Elena Vidal", "Fall 2026", "BC-3030", "#006064"),
        AvailableCourse("Cálculo II", "Prof. Sam Kessler", "Fall 2026", "CAL-4040", "#C07B7B")
    )

    private val _uiState = MutableStateFlow(JoinSubjectUiState())
    val uiState: StateFlow<JoinSubjectUiState> = _uiState.asStateFlow()

    // Backwards compatibility for callers observing availableCourses flow directly
    val availableCourses: StateFlow<List<AvailableCourse>> get() = MutableStateFlow(_uiState.value.availableCourses).asStateFlow()

    init {
        refreshAvailableCourses()
    }

    fun refreshAvailableCourses() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val resource = getTopicsUseCase()) {
                is Resource.Success -> {
                    val existingCourseNames = resource.data.map { topic -> topic.className }.distinct()
                    val filtered = allCourses.filter { course -> course.name !in existingCourseNames }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        availableCourses = filtered,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        availableCourses = allCourses,
                        error = resource.message
                    )
                }
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun refresh() {
        refreshAvailableCourses()
    }

    fun joinCourse(course: AvailableCourse, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isJoining = true, error = null)
            when (val result = addTopicUseCase(course.name)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isJoining = false)
                    refreshAvailableCourses()
                    onDone()
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isJoining = false,
                        error = result.message ?: "Error al unirse al curso"
                    )
                }
                is Resource.Loading -> { }
            }
        }
    }

    fun joinByCode(code: String, onDone: () -> Unit = {}) {
        if (code.isNotBlank()) {
            val name = "Curso Nuevo ($code)"
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isJoining = true, error = null)
                when (val result = addTopicUseCase(name)) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(isJoining = false)
                        refreshAvailableCourses()
                        onDone()
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isJoining = false,
                            error = result.message ?: "Error al unirse con el código"
                        )
                    }
                    is Resource.Loading -> { }
                }
            }
        }
    }
}
