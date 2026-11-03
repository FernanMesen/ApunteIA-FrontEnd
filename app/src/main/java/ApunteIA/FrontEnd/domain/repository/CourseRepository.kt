package ApunteIA.FrontEnd.domain.repository

import ApunteIA.FrontEnd.domain.model.CourseInfo
import ApunteIA.FrontEnd.domain.model.CourseStudent
import ApunteIA.FrontEnd.domain.util.Resource

interface CourseRepository {
    /** Todos los cursos que existen en el backend. */
    suspend fun getAllCourses(): Resource<List<CourseInfo>>

    /** Cursos en los que está inscrito el usuario actual. */
    suspend fun getMyCourses(): Resource<List<CourseInfo>>

    suspend fun enroll(courseId: Long): Resource<Unit>

    // ---------- Solo ADMIN (el backend responde 403 a cualquier otro rol) ----------

    /** Crea el curso si [id] es null; si no, lo actualiza. */
    suspend fun saveCourse(
        id: Long?,
        code: String,
        name: String,
        description: String,
        syllabus: String
    ): Resource<CourseInfo>

    suspend fun getCourseStudents(courseId: Long): Resource<List<CourseStudent>>

    suspend fun removeStudent(enrollmentId: Long): Resource<Unit>
}
