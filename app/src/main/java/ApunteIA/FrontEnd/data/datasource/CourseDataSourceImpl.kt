package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.data.datasource.model.CourseDto
import ApunteIA.FrontEnd.data.datasource.model.CourseRequestDto
import ApunteIA.FrontEnd.data.datasource.model.EnrollUserRequestDto
import ApunteIA.FrontEnd.data.datasource.model.EnrollmentDto
import ApunteIA.FrontEnd.data.remote.ApiService
import ApunteIA.FrontEnd.util.LanguageManager
import retrofit2.HttpException
import javax.inject.Inject

class CourseDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : CourseDataSource {
    override suspend fun getCourses(language: String?): List<CourseDto> =
        apiService.getCourses(language ?: LanguageManager.getCurrentLanguageCode())

    override suspend fun getEnrollments(userId: Long): List<EnrollmentDto> = apiService.getUserEnrollments(userId)

    override suspend fun enroll(courseId: Long, userId: Long): EnrollmentDto =
        apiService.enroll(courseId, EnrollUserRequestDto(userId))

    override suspend fun createCourse(request: CourseRequestDto): CourseDto = apiService.createCourse(request)

    override suspend fun updateCourse(id: Long, request: CourseRequestDto): CourseDto =
        apiService.updateCourse(id, request)

    override suspend fun getCourseEnrollments(courseId: Long): List<EnrollmentDto> =
        apiService.getCourseEnrollments(courseId)

    override suspend fun deleteEnrollment(enrollmentId: Long) {
        val response = apiService.deleteEnrollment(enrollmentId)
        // 404 = ya no estaba inscrito: para el administrador el resultado es el mismo.
        if (!response.isSuccessful && response.code() != 404) throw HttpException(response)
    }
}
