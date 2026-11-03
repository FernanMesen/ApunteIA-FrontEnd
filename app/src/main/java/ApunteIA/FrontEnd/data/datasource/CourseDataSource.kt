package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.data.datasource.model.CourseDto
import ApunteIA.FrontEnd.data.datasource.model.CourseRequestDto
import ApunteIA.FrontEnd.data.datasource.model.EnrollmentDto

interface CourseDataSource {
    suspend fun getCourses(language: String? = null): List<CourseDto>
    suspend fun getEnrollments(userId: Long): List<EnrollmentDto>
    suspend fun enroll(courseId: Long, userId: Long): EnrollmentDto

    // ---------- Solo ADMIN ----------
    suspend fun createCourse(request: CourseRequestDto): CourseDto
    suspend fun updateCourse(id: Long, request: CourseRequestDto): CourseDto
    suspend fun getCourseEnrollments(courseId: Long): List<EnrollmentDto>
    suspend fun deleteEnrollment(enrollmentId: Long)
}
