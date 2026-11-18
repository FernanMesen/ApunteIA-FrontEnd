package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.domain.model.CourseInfo
import ApunteIA.FrontEnd.domain.repository.CourseRepository
import ApunteIA.FrontEnd.domain.util.Resource
import ApunteIA.FrontEnd.util.L10n
import javax.inject.Inject
import java.util.Locale
import kotlin.random.Random

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

class CreateCourseUseCase @Inject constructor(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(
        name: String,
        code: String = "",
        description: String = ""
    ): Resource<CourseInfo> {
        val cleanName = name.trim()
        val cleanDescription = description.trim()
        if (cleanName.isEmpty()) return Resource.Error(L10n.get(R.string.course_name_required))
        if (cleanName.length > 150) return Resource.Error(L10n.get(R.string.course_name_too_long))
        if (cleanDescription.length > 1000) return Resource.Error(L10n.get(R.string.course_description_too_long))

        val normalizedCode = normalizeCode(code)
        if (normalizedCode.length > 50) return Resource.Error(L10n.get(R.string.course_code_too_long))

        val finalCode = if (normalizedCode.isEmpty()) {
            val existing = repository.getAllCourses()
            val existingCodes = when (existing) {
                is Resource.Success -> existing.data.map { normalizeCode(it.code) }.toSet()
                is Resource.Error -> return existing
                is Resource.Loading -> emptySet()
            }
            generateUniqueCode(existingCodes)
        } else normalizedCode

        return repository.createCourse(finalCode, cleanName, cleanDescription)
    }

    companion object {
        fun normalizeCode(code: String): String =
            code.trim()
                .uppercase(Locale.ROOT)
                .replace(Regex("\\s+"), "-")
                .replace(Regex("-{2,}"), "-")

        fun generateUniqueCode(existingCodes: Set<String>): String {
            repeat(20) {
                val number = Random.nextInt(0, 100000)
                val generated = "SUB-%05d".format(Locale.ROOT, number)
                if (normalizeCode(generated) !in existingCodes) return generated
            }
            return "SUB-" + System.nanoTime().toString().takeLast(5)
        }
    }
}
