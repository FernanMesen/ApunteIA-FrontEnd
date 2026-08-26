package ApunteIA.FrontEnd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import ApunteIA.FrontEnd.data.datasource.TopicProvider
import ApunteIA.FrontEnd.presentation.ui.screens.AvailableCourse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class JoinSubjectViewModel : ViewModel() {
    
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
        val existingCourseNames = TopicProvider.topics.map { it.className }.distinct()
        _availableCourses.value = allCourses.filter { it.name !in existingCourseNames }
    }

    fun joinCourse(course: AvailableCourse) {
        TopicProvider.addCourse(course.name)
        refreshAvailableCourses()
    }

    fun joinByCode(code: String) {
        // En un caso real buscaríamos el curso por código
        if (code.isNotBlank()) {
            val name = "Curso Nuevo ($code)"
            TopicProvider.addCourse(name)
            refreshAvailableCourses()
        }
    }
}
