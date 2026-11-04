package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.domain.usecase.GetAvailableCoursesUseCase
import ApunteIA.FrontEnd.domain.usecase.JoinCourseUseCase
import ApunteIA.FrontEnd.domain.util.Resource
import ApunteIA.FrontEnd.fakes.TestBackend
import ApunteIA.FrontEnd.fakes.httpError
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class CourseRepositoryImplTest {

    private fun repository(backend: TestBackend) =
        CourseRepositoryImpl(backend.courses, backend.loader, backend.catalog, backend.quizzes)

    @Test
    fun `my courses come from the enrollments`() = runBlocking {
        val backend = TestBackend()

        val mine = (repository(backend).getMyCourses() as Resource.Success).data

        assertEquals(listOf("EIF411", "EIF412"), mine.map { it.code })
        assertEquals("Persistencia y servicios backend", mine.first().description)
    }

    @Test
    fun `available courses exclude the ones I am enrolled in`() = runBlocking {
        val backend = TestBackend()

        val available = (GetAvailableCoursesUseCase(repository(backend))() as Resource.Success).data

        assertEquals(listOf("EIF413"), available.map { it.code })
    }

    @Test
    fun `join by code enrolls the logged in user`() = runBlocking {
        val backend = TestBackend()

        val result = JoinCourseUseCase(repository(backend)).byCode("eif413")

        assertEquals("Programación distribuida", (result as Resource.Success).data.name)
        assertEquals(listOf(3L to 2L), backend.courses.enrolled)
    }

    @Test
    fun `join by unknown code fails without calling the backend`() = runBlocking {
        val backend = TestBackend()

        val result = JoinCourseUseCase(repository(backend)).byCode("XYZ999")

        assertEquals("No existe un curso con el código \"XYZ999\".", (result as Resource.Error).message)
        assertEquals(emptyList<Pair<Long, Long>>(), backend.courses.enrolled)
    }

    @Test
    fun `already enrolled shows the backend message`() = runBlocking {
        val backend = TestBackend()
        backend.courses.enrollError = httpError(409, "El usuario 2 ya está inscrito en el curso 1")

        val result = repository(backend).enroll(1)

        assertEquals("El usuario 2 ya está inscrito en el curso 1", (result as Resource.Error).message)
    }
}