package ApunteIA.FrontEnd.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.domain.model.CourseStudent
import ApunteIA.FrontEnd.domain.repository.CourseRepository
import ApunteIA.FrontEnd.domain.usecase.GetCourseStudentsUseCase
import ApunteIA.FrontEnd.domain.usecase.RemoveStudentFromCourseUseCase
import ApunteIA.FrontEnd.domain.usecase.SaveCourseUseCase
import ApunteIA.FrontEnd.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminCourseDetailUiState(
    /** null mientras se crea un curso nuevo. */
    val courseId: Long? = null,
    val code: String = "",
    val name: String = "",
    val description: String = "",
    val syllabus: String = "",
    val students: List<CourseStudent> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    /** Inscripción que se está quitando (para mostrar el progreso en esa fila). */
    val removingEnrollmentId: Long? = null,
    /** Error de validación local (texto en strings.xml). */
    @StringRes val validationError: Int? = null,
    /** Error que viene del backend o de la red (ya traducido o con el mensaje del backend). */
    val error: String? = null,
    /** Se pone en true al guardar; la pantalla muestra un aviso y lo consume. */
    val savedEvent: Boolean = false
) {
    val isNew: Boolean get() = courseId == null
}

/**
 * Administración de un curso: datos, plan de estudios (un tema por línea) y
 * estudiantes inscritos. Ruta `admin_course/{courseId}`; courseId = 0 crea uno nuevo.
 */
@HiltViewModel
class AdminCourseDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val courseRepository: CourseRepository,
    private val saveCourse: SaveCourseUseCase,
    private val getCourseStudents: GetCourseStudentsUseCase,
    private val removeStudentFromCourse: RemoveStudentFromCourseUseCase
) : ViewModel() {

    private val initialId: Long? = savedStateHandle.get<Long>(COURSE_ID_ARG)?.takeIf { it > 0L }

    private val _uiState = MutableStateFlow(AdminCourseDetailUiState(courseId = initialId))
    val uiState: StateFlow<AdminCourseDetailUiState> = _uiState.asStateFlow()

    init {
        if (initialId != null) load()
    }

    fun load() {
        val id = _uiState.value.courseId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val courses = courseRepository.getAllCourses()) {
                is Resource.Success -> {
                    val course = courses.data.firstOrNull { it.id == id }
                    _uiState.update { state ->
                        if (course == null) {
                            state.copy(isLoading = false)
                        } else {
                            state.copy(
                                code = course.code,
                                name = course.name,
                                description = course.description,
                                syllabus = course.syllabus
                            )
                        }
                    }
                    loadStudents(id)
                }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = courses.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    private suspend fun loadStudents(courseId: Long) {
        when (val result = getCourseStudents(courseId)) {
            is Resource.Success -> _uiState.update { it.copy(isLoading = false, students = result.data) }
            is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
            is Resource.Loading -> Unit
        }
    }

    fun onCodeChange(value: String) = _uiState.update { it.copy(code = value.take(MAX_CODE), validationError = null) }
    fun onNameChange(value: String) = _uiState.update { it.copy(name = value.take(MAX_NAME), validationError = null) }
    fun onDescriptionChange(value: String) =
        _uiState.update { it.copy(description = value.take(MAX_DESCRIPTION), validationError = null) }
    fun onSyllabusChange(value: String) =
        _uiState.update { it.copy(syllabus = value.take(MAX_SYLLABUS), validationError = null) }

    fun save() {
        val state = _uiState.value
        if (state.isSaving) return
        val validation = when {
            state.code.isBlank() -> R.string.admin_error_code_required
            state.name.isBlank() -> R.string.admin_error_name_required
            else -> null
        }
        if (validation != null) {
            _uiState.update { it.copy(validationError = validation) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, validationError = null) }
            when (val result = saveCourse(state.courseId, state.code, state.name, state.description, state.syllabus)) {
                is Resource.Success -> {
                    val course = result.data
                    _uiState.update {
                        it.copy(
                            courseId = course.id,
                            code = course.code,
                            name = course.name,
                            description = course.description,
                            syllabus = course.syllabus,
                            isSaving = false,
                            savedEvent = true
                        )
                    }
                }
                is Resource.Error -> _uiState.update { it.copy(isSaving = false, error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun removeStudent(student: CourseStudent) {
        if (_uiState.value.removingEnrollmentId != null) return
        viewModelScope.launch {
            _uiState.update { it.copy(removingEnrollmentId = student.enrollmentId, error = null) }
            when (val result = removeStudentFromCourse(student.enrollmentId)) {
                is Resource.Success -> _uiState.update { state ->
                    state.copy(
                        removingEnrollmentId = null,
                        students = state.students.filterNot { it.enrollmentId == student.enrollmentId }
                    )
                }
                is Resource.Error -> _uiState.update { it.copy(removingEnrollmentId = null, error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun onSavedEventHandled() = _uiState.update { it.copy(savedEvent = false) }

    fun dismissError() = _uiState.update { it.copy(error = null) }

    companion object {
        const val COURSE_ID_ARG = "courseId"

        // Mismos límites que valida el backend.
        const val MAX_CODE = 50
        const val MAX_NAME = 150
        const val MAX_DESCRIPTION = 1000
        const val MAX_SYLLABUS = 5000
    }
}
