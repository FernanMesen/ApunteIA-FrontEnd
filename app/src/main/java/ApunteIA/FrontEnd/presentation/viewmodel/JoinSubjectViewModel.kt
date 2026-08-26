package ApunteIA.FrontEnd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import ApunteIA.FrontEnd.data.datasource.TopicProvider
import ApunteIA.FrontEnd.presentation.ui.screens.AvailableCourse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class JoinSubjectViewModel : ViewModel() {
    
    private val _availableCourses = MutableStateFlow(
        listOf(
            AvailableCourse("Álgebra Lineal", "Dr. Nora Belén", "Fall 2026", "LIN-1180", "#2E3B3B"),
            AvailableCourse("Introducción a la Psicología", "Prof. Iván Duarte", "Fall 2026", "PSY-1010", "#8FB2C9")
        )
    )
    val availableCourses: StateFlow<List<AvailableCourse>> = _availableCourses.asStateFlow()

    fun joinCourse(course: AvailableCourse) {
        TopicProvider.addCourse(course.name)
    }

    fun joinByCode(code: String) {
        // En un caso real buscaríamos el curso por código
        if (code.isNotBlank()) {
            TopicProvider.addCourse("Curso Nuevo ($code)")
        }
    }
}
