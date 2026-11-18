package ApunteIA.FrontEnd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ApunteIA.FrontEnd.domain.usecase.CreateCourseUseCase
import ApunteIA.FrontEnd.domain.model.Course
import ApunteIA.FrontEnd.domain.model.Topic
import ApunteIA.FrontEnd.domain.usecase.GetMyCoursesUseCase
import ApunteIA.FrontEnd.domain.usecase.GetTopicsUseCase
import ApunteIA.FrontEnd.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CoursesUiState(
    val isLoading: Boolean = false,
    val courses: List<Course> = emptyList(),
    val error: String? = null,
    val isCreating: Boolean = false,
    val createError: String? = null,
    val createSuccess: Boolean = false
)

/** Colores fijos para distinguir los cursos (se asignan por id). */
val COURSE_COLORS = listOf("#0A3326", "#006064", "#C07B7B", "#2E3B3B", "#8FB2C9", "#5C6BC0", "#8D6E63")

fun courseColor(courseId: Long): String = COURSE_COLORS[(courseId % COURSE_COLORS.size).toInt()]

/** Materias en las que está inscrito el usuario, con su progreso según la auditoría. */
@HiltViewModel
class ManageSubjectsViewModel @Inject constructor(
    private val getMyCoursesUseCase: GetMyCoursesUseCase,
    private val getTopicsUseCase: GetTopicsUseCase,
    private val createCourseUseCase: CreateCourseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoursesUiState())
    val uiState: StateFlow<CoursesUiState> = _uiState.asStateFlow()

    // La pantalla llama a refresh() cada vez que vuelve a mostrarse (RefreshOnResume).

    fun loadCourses() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val topicsDeferred = async { getTopicsUseCase() }
            when (val resource = getMyCoursesUseCase()) {
                is Resource.Success -> {
                    val topics: List<Topic> = (topicsDeferred.await() as? Resource.Success)?.data.orEmpty()
                    val courses = resource.data.map { course ->
                        val courseTopics = topics.filter { it.className == course.name }
                        Course(
                            name = course.name,
                            professor = course.code,
                            term = course.description,
                            progress = if (courseTopics.isEmpty()) 0 else courseTopics.sumOf { it.progress } / courseTopics.size,
                            colorHex = courseColor(course.id),
                            syllabus = course.syllabusTopics
                        )
                    }
                    _uiState.value = CoursesUiState(isLoading = false, courses = courses, error = null)
                }
                is Resource.Error -> {
                    topicsDeferred.cancel()
                    _uiState.value = _uiState.value.copy(isLoading = false, error = resource.message)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun refresh() {
        loadCourses()
    }

    fun createCourse(name: String, code: String, description: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isCreating = true, createError = null, createSuccess = false) }
            when (val result = createCourseUseCase(name, code, description)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isCreating = false, createError = null, createSuccess = true) }
                    loadCourses()
                }
                is Resource.Error -> _uiState.update {
                    it.copy(isCreating = false, createError = result.message, createSuccess = false)
                }
                is Resource.Loading -> Unit
            }
        }
    }
}