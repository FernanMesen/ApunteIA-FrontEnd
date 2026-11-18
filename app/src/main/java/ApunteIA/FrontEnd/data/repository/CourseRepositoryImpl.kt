package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.data.datasource.CourseDataSource
import ApunteIA.FrontEnd.data.datasource.model.CourseRequestDto
import ApunteIA.FrontEnd.data.datasource.model.EnrollmentDto
import ApunteIA.FrontEnd.data.mapper.toDomain
import ApunteIA.FrontEnd.data.util.safeApiCall
import ApunteIA.FrontEnd.data.util.toFriendlyMessage
import ApunteIA.FrontEnd.data.util.backendMessage
import ApunteIA.FrontEnd.domain.model.CourseInfo
import ApunteIA.FrontEnd.domain.model.CourseStudent
import ApunteIA.FrontEnd.domain.repository.CourseRepository
import ApunteIA.FrontEnd.domain.util.Resource
import ApunteIA.FrontEnd.util.L10n
import javax.inject.Inject
import retrofit2.HttpException

import ApunteIA.FrontEnd.data.datasource.QuizDataSource
import ApunteIA.FrontEnd.data.datasource.model.AssessmentDto

class CourseRepositoryImpl @Inject constructor(
    private val dataSource: CourseDataSource,
    private val loader: StudyDataLoader,
    private val catalog: CourseCatalog,
    private val quizDataSource: QuizDataSource
) : CourseRepository {

    private val errorMapper: (Throwable) -> String = { throwable ->
        val rawMessage = throwable.backendMessage() ?: throwable.toFriendlyMessage(L10n.get(R.string.error_loading_courses))
        if (rawMessage.contains("constraint", ignoreCase = true) ||
            rawMessage.contains("violates", ignoreCase = true) ||
            rawMessage.contains("foreign key", ignoreCase = true) ||
            rawMessage.contains("restricción", ignoreCase = true) ||
            rawMessage.contains("integrity", ignoreCase = true) ||
            rawMessage.contains("violación", ignoreCase = true)) {
            L10n.get(R.string.admin_error_course_has_relations)
        } else {
            rawMessage
        }
    }

    override suspend fun getAllCourses(): Resource<List<CourseInfo>> = safeApiCall(errorMapper) {
        catalog.all(forceRefresh = true)
    }

    override suspend fun getMyCourses(): Resource<List<CourseInfo>> = safeApiCall(errorMapper) {
        loader.myCourses()
    }

    override suspend fun enroll(courseId: Long): Resource<Unit> = safeApiCall(
        errorMapper = { it.toFriendlyMessage(L10n.get(R.string.error_enroll_course)) }
    ) {
        dataSource.enroll(courseId, loader.requireUserId())
        loader.invalidate()
        Unit
    }

    override suspend fun createCourse(code: String, name: String, description: String): Resource<CourseInfo> =
        safeApiCall(errorMapper) {
            val saved = try {
                dataSource.createCourse(
                    CourseRequestDto(
                        code = code.trim(),
                        name = name.trim(),
                        description = description.trim().ifBlank { null },
                        syllabus = null
                    )
                )
            } catch (e: HttpException) {
                if (e.code() == 409) {
                    throw IllegalStateException(
                        e.backendMessage() ?: L10n.get(R.string.course_code_duplicate)
                    )
                }
                throw e
            }
            val savedCourse = saved.toDomain()

            loader.invalidate()
            try {
                catalog.all(forceRefresh = true)
            } catch (_: Exception) {
                // A successful 201 remains successful even if a secondary refresh fails.
            }
            savedCourse
        }

    // ---------- Solo ADMIN ----------

    override suspend fun saveCourse(
        id: Long?,
        code: String,
        name: String,
        description: String,
        syllabus: String
    ): Resource<CourseInfo> = safeApiCall(errorMapper) {
        val request = CourseRequestDto(
            code = code.trim(),
            name = name.trim(),
            description = description.trim().ifBlank { null },
            syllabus = syllabus.trim().ifBlank { null }
        )
        val saved = if (id == null) dataSource.createCourse(request) else dataSource.updateCourse(id, request)
        // El catálogo y el tablero guardan cursos en memoria: se recargan con los datos nuevos.
        catalog.all(forceRefresh = true)
        loader.invalidate()
        saved.toDomain()
    }

    /** Inscritos del curso, sin nota: la calcula GetCourseStudentsUseCase con CourseGrades. */
    override suspend fun getCourseStudents(courseId: Long): Resource<List<CourseStudent>> =
        safeApiCall(errorMapper) {
            val enrollments: List<EnrollmentDto> = dataSource.getCourseEnrollments(courseId)
            enrollments.map { enrollment ->
                CourseStudent(
                    enrollmentId = enrollment.id,
                    userId = enrollment.userId ?: 0L,
                    fullName = enrollment.userFullName.orEmpty(),
                    email = enrollment.userEmail.orEmpty()
                )
            }.sortedBy { it.fullName.lowercase() }
        }

    override suspend fun deleteCourse(id: Long): Resource<String> = safeApiCall(errorMapper) {
        val students = dataSource.getCourseEnrollments(id)
        val assessments = try { quizDataSource.getCourseAssessments(id) } catch (_: Exception) { emptyList<AssessmentDto>() }

        if (students.isNotEmpty() || assessments.isNotEmpty()) {
            val course = catalog.all(false).firstOrNull { it.id == id }
            if (course != null && !course.name.startsWith("[Archivado]")) {
                val request = CourseRequestDto(
                    code = course.code,
                    name = "[Archivado] ${course.name}".take(150),
                    description = course.description,
                    syllabus = course.syllabus
                )
                dataSource.updateCourse(id, request)
                catalog.all(forceRefresh = true)
                loader.invalidate()
                return@safeApiCall L10n.get(R.string.admin_course_archived)
            }
        }

        try {
            dataSource.deleteCourse(id)
        } catch (e: Exception) {
            val course = catalog.all(false).firstOrNull { it.id == id }
            if (course != null && !course.name.startsWith("[Archivado]")) {
                val request = CourseRequestDto(
                    code = course.code,
                    name = "[Archivado] ${course.name}".take(150),
                    description = course.description,
                    syllabus = course.syllabus
                )
                dataSource.updateCourse(id, request)
                catalog.all(forceRefresh = true)
                loader.invalidate()
                return@safeApiCall L10n.get(R.string.admin_course_archived)
            } else {
                throw e
            }
        }
        catalog.all(forceRefresh = true)
        loader.invalidate()
        L10n.get(R.string.admin_course_deleted)
    }

    override suspend fun removeStudent(enrollmentId: Long): Resource<Unit> = safeApiCall(errorMapper) {
        dataSource.deleteEnrollment(enrollmentId)
        loader.invalidate()
    }
}
