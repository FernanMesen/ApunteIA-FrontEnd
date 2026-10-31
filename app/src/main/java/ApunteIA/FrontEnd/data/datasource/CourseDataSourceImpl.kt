package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.data.datasource.model.CourseDto
import ApunteIA.FrontEnd.data.datasource.model.EnrollUserRequestDto
import ApunteIA.FrontEnd.data.datasource.model.EnrollmentDto
import ApunteIA.FrontEnd.data.remote.ApiService
import javax.inject.Inject

class CourseDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : CourseDataSource {
    override suspend fun getCourses(): List<CourseDto> = apiService.getCourses()

    override suspend fun getEnrollments(userId: Long): List<EnrollmentDto> = apiService.getUserEnrollments(userId)

    override suspend fun enroll(courseId: Long, userId: Long): EnrollmentDto =
        apiService.enroll(courseId, EnrollUserRequestDto(userId))
}