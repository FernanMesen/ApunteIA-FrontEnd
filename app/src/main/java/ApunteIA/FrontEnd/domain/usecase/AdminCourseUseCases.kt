package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.model.CourseInfo
import ApunteIA.FrontEnd.domain.model.CourseStudent
import ApunteIA.FrontEnd.domain.repository.CourseRepository
import ApunteIA.FrontEnd.domain.util.Resource
import javax.inject.Inject

/** Crea (id = null) o actualiza un curso con su plan de estudios. Solo ADMIN. */
class SaveCourseUseCase @Inject constructor(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(
        id: Long?,
        code: String,
        name: String,
        description: String,
        syllabus: String
    ): Resource<CourseInfo> = repository.saveCourse(id, code, name, description, syllabus)
}

/** Estudiantes inscritos en un curso. */
class GetCourseStudentsUseCase @Inject constructor(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(courseId: Long): Resource<List<CourseStudent>> =
        repository.getCourseStudents(courseId)
}

/** Quita a un estudiante de un curso (borra la inscripción). Solo ADMIN. */
class RemoveStudentFromCourseUseCase @Inject constructor(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(enrollmentId: Long): Resource<Unit> = repository.removeStudent(enrollmentId)
}
