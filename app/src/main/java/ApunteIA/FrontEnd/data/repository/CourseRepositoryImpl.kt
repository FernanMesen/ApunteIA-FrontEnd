package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.data.datasource.CourseDataSource
import ApunteIA.FrontEnd.data.datasource.model.CourseRequestDto
import ApunteIA.FrontEnd.data.datasource.model.EnrollmentDto
import ApunteIA.FrontEnd.data.mapper.toDomain
import ApunteIA.FrontEnd.data.util.safeApiCall
import ApunteIA.FrontEnd.data.util.toFriendlyMessage
import ApunteIA.FrontEnd.domain.model.CourseInfo
import ApunteIA.FrontEnd.domain.model.CourseStudent
import ApunteIA.FrontEnd.domain.repository.CourseRepository
import ApunteIA.FrontEnd.domain.util.Resource
import ApunteIA.FrontEnd.util.L10n
import javax.inject.Inject

class CourseRepositoryImpl @Inject constructor(
    private val dataSource: CourseDataSource,
    private val loader: StudyDataLoader,
    private val catalog: CourseCatalog
) : CourseRepository {

    private val errorMapper: (Throwable) -> String = { it.toFriendlyMessage(L10n.get(R.string.error_loading_courses)) }

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
                CourseStudent(
                    enrollmentId = enrollment.id,
                    userId = enrollment.userId ?: 0L,
                    fullName = enrollment.userFullName.orEmpty(),
                    email = enrollment.userEmail.orEmpty()
                )
            }.sortedBy { it.fullName.lowercase() }
        }

    override suspend fun removeStudent(enrollmentId: Long): Resource<Unit> = safeApiCall(errorMapper) {
        dataSource.deleteEnrollment(enrollmentId)
        loader.invalidate()
    }
}
