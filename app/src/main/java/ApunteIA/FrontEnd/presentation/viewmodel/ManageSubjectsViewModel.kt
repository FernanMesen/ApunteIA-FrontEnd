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

@HiltViewModel
class ManageSubjectsViewModel @Inject constructor(
    private val getTopicsUseCase: GetTopicsUseCase
) : ViewModel() {

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    init {
        loadCourses()
    }

    fun loadCourses() {
        viewModelScope.launch {
            when (val resource = getTopicsUseCase()) {
                is Resource.Success -> {
                    val grouped = resource.data.groupBy { it.className }
                    val courseList = grouped.map { (className, classTopics) ->
                        val avgProgress = classTopics.map { it.progress }.average().toInt()

                        // Mapeo de datos extras (Profesor, Color, etc.) basado en el nombre de la clase
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

                        Course(className, professor, term, avgProgress, colorHex)
                    }
                    _courses.value = courseList
                }
                is Resource.Error -> {
                    _courses.value = emptyList()
                }
                is Resource.Loading -> {
                    // No-op: esta pantalla no muestra un estado de carga separado.
                }
            }
        }
    }
}