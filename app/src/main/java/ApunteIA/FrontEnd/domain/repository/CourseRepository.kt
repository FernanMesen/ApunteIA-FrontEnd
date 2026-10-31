package ApunteIA.FrontEnd.domain.repository

import ApunteIA.FrontEnd.domain.model.CourseInfo
import ApunteIA.FrontEnd.domain.util.Resource

interface CourseRepository {
    /** Todos los cursos que existen en el backend. */
    suspend fun getAllCourses(): Resource<List<CourseInfo>>

    /** Cursos en los que está inscrito el usuario actual. */
    suspend fun getMyCourses(): Resource<List<CourseInfo>>

    suspend fun enroll(courseId: Long): Resource<Unit>
}