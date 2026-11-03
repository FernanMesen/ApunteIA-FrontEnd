package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.model.EnrollmentDto
import ApunteIA.FrontEnd.domain.util.Resource
import ApunteIA.FrontEnd.fakes.TestBackend
import ApunteIA.FrontEnd.fakes.httpError
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AdminCourseRepositoryTest {

    private fun repository(backend: TestBackend) =
        CourseRepositoryImpl(backend.courses, backend.loader, backend.catalog)

    @Test
    fun `create course sends trimmed data and the study plan`() = runBlocking {
        val backend = TestBackend()

        val result = repository(backend).saveCourse(
            id = null,
            code = "  EIF420 ",
            name = " Móviles ",
            description = "  ",
            syllabus = "Kotlin\nCompose\n"
        )

        val course = (result as Resource.Success).data
        assertEquals("EIF420", course.code)
        assertEquals(listOf("Kotlin", "Compose"), course.syllabusTopics)
        val (id, request) = backend.courses.savedCourses.single()
        assertNull(id)
        assertNull(request.description)
        assertEquals("Kotlin\nCompose", request.syllabus)
    }

    @Test
    fun `update course refreshes the catalog with the new study plan`() = runBlocking {
        val backend = TestBackend()
        val repository = repository(backend)

        repository.saveCourse(1, "EIF411", "Backend para plataformas móviles", "Persistencia", "- JPA\n• REST")

        val all = (repository.getAllCourses() as Resource.Success).data
        assertEquals(listOf("JPA", "REST"), all.first { it.id == 1L }.syllabusTopics)
    }

    @Test
    fun `students of a course come sorted by name and can be removed`() = runBlocking {
        val backend = TestBackend()
        backend.courses.courseEnrollments = listOf(
            EnrollmentDto(10, 2, 1, "EIF411", "Backend", null, "Zoe Mora", "zoe@una.cr"),
            EnrollmentDto(11, 3, 1, "EIF411", "Backend", null, "Ana Solís", "ana@una.cr"),
            EnrollmentDto(12, 4, 2, "EIF412", "BD", null, "Otro Curso", "otro@una.cr")
        )
        val repository = repository(backend)

        val students = (repository.getCourseStudents(1) as Resource.Success).data
        assertEquals(listOf("Ana Solís", "Zoe Mora"), students.map { it.fullName })

        assertTrue(repository.removeStudent(11) is Resource.Success)
        assertEquals(listOf(11L), backend.courses.deletedEnrollments)
        assertEquals(listOf("Zoe Mora"), (repository.getCourseStudents(1) as Resource.Success).data.map { it.fullName })
    }

    @Test
    fun `a student trying to create a course gets the permission message`() = runBlocking {
        val backend = TestBackend()
        val repository = object : ApunteIA.FrontEnd.data.datasource.CourseDataSource by backend.courses {
            override suspend fun createCourse(request: ApunteIA.FrontEnd.data.datasource.model.CourseRequestDto) =
                throw httpError(403, "No tiene permisos para realizar esta acción")
        }.let { CourseRepositoryImpl(it, backend.loader, backend.catalog) }

        val result = repository.saveCourse(null, "EIF999", "X", "", "")

        assertEquals("No tiene permisos para realizar esta acción", (result as Resource.Error).message)
    }
}
