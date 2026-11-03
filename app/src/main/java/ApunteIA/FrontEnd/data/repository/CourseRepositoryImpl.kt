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

import ApunteIA.FrontEnd.data.datasource.QuizDataSource

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

    override suspend fun getCourseStudents(courseId: Long): Resource<List<CourseStudent>> =
        safeApiCall(errorMapper) {
            val enrollments: List<EnrollmentDto> = dataSource.getCourseEnrollments(courseId)
            enrollments.map { enrollment ->
                val userId = enrollment.userId ?: 0L
                val avgGrade = try {
                    if (userId > 0L) {
                        val attempts = quizDataSource.getUserAttempts(userId)
                        val completed = attempts.filter { it.status.equals("COMPLETED", ignoreCase = true) && it.score != null }
                        if (completed.isNotEmpty()) completed.mapNotNull { it.score }.average() else null
                    } else null
                } catch (_: Exception) {
                    null
                }
                CourseStudent(
                    enrollmentId = enrollment.id,
                    userId = userId,
                    fullName = enrollment.userFullName.orEmpty(),
                    email = enrollment.userEmail.orEmpty(),
                    averageGrade = avgGrade
                )
            }.sortedBy { it.fullName.lowercase() }
        }

    override suspend fun deleteCourse(id: Long): Resource<String> = safeApiCall(errorMapper) {
        val students = dataSource.getCourseEnrollments(id)
        val assessments = try { quizDataSource.getCourseAssessments(id) } catch (_: Exception) { emptyList<Any>() }

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
