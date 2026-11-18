package ApunteIA.FrontEnd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ApunteIA.FrontEnd.domain.usecase.GetAvailableCoursesUseCase
import ApunteIA.FrontEnd.domain.usecase.JoinCourseUseCase
import ApunteIA.FrontEnd.domain.usecase.CreateCourseUseCase
import ApunteIA.FrontEnd.domain.repository.CourseRepository
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
    val totalCatalogCount: Int = 0,
    val joiningCourseId: Long? = null,
    val isJoiningByCode: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val isCreating: Boolean = false,
    val createError: String? = null
)

/** Cursos del backend a los que el usuario aún no se ha unido (POST /courses/{id}/enrollments). */
@HiltViewModel
class JoinSubjectViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val getAvailableCoursesUseCase: GetAvailableCoursesUseCase,
    private val joinCourseUseCase: JoinCourseUseCase,
    private val createCourseUseCase: CreateCourseUseCase
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
                    val total = when (val all = courseRepository.getAllCourses()) {
                        is Resource.Success -> all.data.size
                        else -> 0
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        availableCourses = resource.data.map { course ->
                            AvailableCourse(
                                name = course.name,
                                id = course.code,
                                colorHex = courseColor(course.id),
                                courseId = course.id,
                                code = course.code,
                                description = course.description,
                                createdByName = course.createdByName,
                                studentCount = course.studentCount
                            )
                        },
                        totalCatalogCount = total,
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
            _uiState.value = _uiState.value.copy(joiningCourseId = course.courseId, error = null)
            when (val result = joinCourseUseCase(course.courseId)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        joiningCourseId = null,
                        availableCourses = _uiState.value.availableCourses.filterNot { it.courseId == course.courseId }
                    )
                    onDone()
                    refreshAvailableCourses()
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(joiningCourseId = null, error = result.message)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun joinByCode(code: String, onDone: () -> Unit = {}) {
        if (code.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isJoiningByCode = true, error = null)
            when (val result = joinCourseUseCase.byCode(code)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isJoiningByCode = false)
                    onDone()
                    refreshAvailableCourses()
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isJoiningByCode = false, error = result.message)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun createCourse(name: String, code: String, description: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreating = true, createError = null)
            when (val result = createCourseUseCase(name, code, description)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isCreating = false)
                    onDone()
                    refreshAvailableCourses()
                }
                is Resource.Error -> _uiState.value =
                    _uiState.value.copy(isCreating = false, createError = result.message)
                is Resource.Loading -> Unit
            }
        }
    }
}