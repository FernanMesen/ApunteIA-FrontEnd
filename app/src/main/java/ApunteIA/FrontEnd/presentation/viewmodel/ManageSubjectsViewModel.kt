package ApunteIA.FrontEnd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ApunteIA.FrontEnd.domain.model.Course
import ApunteIA.FrontEnd.domain.usecase.GetTopicsUseCase
import ApunteIA.FrontEnd.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CoursesUiState(
    val isLoading: Boolean = false,
    val courses: List<Course> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ManageSubjectsViewModel @Inject constructor(
    private val getTopicsUseCase: GetTopicsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoursesUiState())
    val uiState: StateFlow<CoursesUiState> = _uiState.asStateFlow()

    init {
        loadCourses()
    }

    fun loadCourses() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            when (val resource = getTopicsUseCase()) {
                is Resource.Success -> {
                    val grouped = resource.data.groupBy { it.className }

                    val courseList = grouped.map { (className, classTopics) ->
                        val avgProgress = if (classTopics.isNotEmpty()) {
                            classTopics.map { it.progress }.average().toInt()
                        } else 0

                        val professor = when (className) {
                            "Química Orgánica" -> "Dr. Alan Reyes"
                            "Biología Celular" -> "Dr. Elena Vidal"
                            "Cálculo II" -> "Prof. Sam Kessler"
                            else -> "Profesor Asignado"
                        }

                        val term = "Fall 2026"

                        val colorHex = when (className) {
                            "Química Orgánica" -> "#0A3326"
                            "Biología Celular" -> "#006064"
                            "Cálculo II" -> "#C07B7B"
                            else -> "#757575"
                        }

                        Course(
                            name = className,
                            professor = professor,
                            term = term,
                            progress = avgProgress,
                            colorHex = colorHex
                        )
                    }

                    _uiState.value = CoursesUiState(
                        isLoading = false,
                        courses = courseList,
                        error = null
                    )
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = resource.message
                    )
                }

                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = true
                    )
                }
            }
        }
    }

    fun refresh() {
        loadCourses()
    }
}
