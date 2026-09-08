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

    private val _availableCourses = MutableStateFlow<List<AvailableCourse>>(emptyList())
    val availableCourses: StateFlow<List<AvailableCourse>> = _availableCourses.asStateFlow()

    init {
        refreshAvailableCourses()
    }

    private fun refreshAvailableCourses() {
        viewModelScope.launch {
            when (val resource = getTopicsUseCase()) {
                is Resource.Success -> {
                    val existingCourseNames = resource.data.map { topic -> topic.className }.distinct()
                    _availableCourses.value = allCourses.filter { course -> course.name !in existingCourseNames }
                }
                is Resource.Error -> {
                    _availableCourses.value = allCourses
                }
                is Resource.Loading -> {
                    // No-op: esta pantalla no muestra un estado de carga separado.
                }
            }
        }
    }

    fun joinCourse(course: AvailableCourse) {
        viewModelScope.launch {
            addTopicUseCase(course.name)
            refreshAvailableCourses()
        }
    }

    fun joinByCode(code: String) {
        if (code.isNotBlank()) {
            val name = "Curso Nuevo ($code)"
            viewModelScope.launch {
                addTopicUseCase(name)
                refreshAvailableCourses()
            }
        }
    }
}