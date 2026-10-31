package ApunteIA.FrontEnd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ApunteIA.FrontEnd.domain.usecase.GetAvailableCoursesUseCase
import ApunteIA.FrontEnd.domain.usecase.JoinCourseUseCase
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

/** Cursos del backend a los que el usuario aún no se ha unido (POST /courses/{id}/enrollments). */
@HiltViewModel
class JoinSubjectViewModel @Inject constructor(
    private val getAvailableCoursesUseCase: GetAvailableCoursesUseCase,
    private val joinCourseUseCase: JoinCourseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(JoinSubjectUiState())
    val uiState: StateFlow<JoinSubjectUiState> = _uiState.asStateFlow()

    init {
        refreshAvailableCourses()
    }

    fun refreshAvailableCourses() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val resource = getAvailableCoursesUseCase()) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        availableCourses = resource.data.map { course ->
                            AvailableCourse(
                                name = course.name,
                                professor = course.description,
                                term = "",
                                id = course.code,
                                colorHex = courseColor(course.id),
                                courseId = course.id
                            )
                        },
                        error = null
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = resource.message)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun refresh() {
        refreshAvailableCourses()
    }

    fun joinCourse(course: AvailableCourse, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isJoining = true, error = null)
            when (val result = joinCourseUseCase(course.courseId)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isJoining = false)
                    onDone()
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isJoining = false, error = result.message)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun joinByCode(code: String, onDone: () -> Unit = {}) {
        if (code.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isJoining = true, error = null)
            when (val result = joinCourseUseCase.byCode(code)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isJoining = false)
                    onDone()
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isJoining = false, error = result.message)
                }
                is Resource.Loading -> Unit
            }
        }
    }
}