package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.domain.model.CourseInfo
import ApunteIA.FrontEnd.domain.repository.CourseRepository
import ApunteIA.FrontEnd.domain.util.Resource
import ApunteIA.FrontEnd.util.L10n
import javax.inject.Inject

class GetMyCoursesUseCase @Inject constructor(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(): Resource<List<CourseInfo>> = repository.getMyCourses()
}

/** Cursos a los que el usuario todavía no se ha unido. */
class GetAvailableCoursesUseCase @Inject constructor(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(): Resource<List<CourseInfo>> {
        val all = repository.getAllCourses()
        if (all !is Resource.Success) return all
        val mine = repository.getMyCourses()
        if (mine !is Resource.Success) return mine
        val myIds = mine.data.map { it.id }.toSet()
        return Resource.Success(all.data.filter { it.id !in myIds })
    }
}

class JoinCourseUseCase @Inject constructor(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(courseId: Long): Resource<Unit> = repository.enroll(courseId)

    /** Busca el curso por su código (p. ej. "EIF411") y se inscribe. */
    suspend fun byCode(code: String): Resource<CourseInfo> {
        val wanted = code.trim()
        val all = repository.getAllCourses()
        if (all !is Resource.Success) {
            return Resource.Error((all as? Resource.Error)?.message ?: L10n.get(R.string.error_loading_courses))
        }
        val course = all.data.firstOrNull { it.code.equals(wanted, ignoreCase = true) }
            ?: return Resource.Error(L10n.get(R.string.error_course_not_found, wanted))
        return when (val result = repository.enroll(course.id)) {
            is Resource.Success -> Resource.Success(course)
            is Resource.Error -> Resource.Error(result.message, result.throwable)
            is Resource.Loading -> Resource.Loading
        }
    }
}
