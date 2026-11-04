package ApunteIA.FrontEnd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ApunteIA.FrontEnd.domain.model.CourseInfo
import ApunteIA.FrontEnd.domain.repository.CourseRepository
import ApunteIA.FrontEnd.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminCoursesUiState(
    val isLoading: Boolean = false,
    val courses: List<CourseInfo> = emptyList(),
    val error: String? = null
)

/** Administración: lista de todos los cursos del sistema. */
@HiltViewModel
class AdminCoursesViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminCoursesUiState())
    val uiState: StateFlow<AdminCoursesUiState> = _uiState.asStateFlow()

    // La pantalla llama a load() cada vez que vuelve a mostrarse (RefreshOnResume).
    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = courseRepository.getAllCourses()) {
                is Resource.Success -> _uiState.update {
                    it.copy(isLoading = false, courses = result.data.sortedBy { course -> course.code })
                }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }
}
