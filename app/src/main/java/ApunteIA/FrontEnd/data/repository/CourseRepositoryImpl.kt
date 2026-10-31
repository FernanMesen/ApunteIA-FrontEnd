package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.CourseDataSource
import ApunteIA.FrontEnd.data.util.safeApiCall
import ApunteIA.FrontEnd.data.util.toFriendlyMessage
import ApunteIA.FrontEnd.domain.model.CourseInfo
import ApunteIA.FrontEnd.domain.repository.CourseRepository
import ApunteIA.FrontEnd.domain.util.Resource
import javax.inject.Inject

class CourseRepositoryImpl @Inject constructor(
    private val dataSource: CourseDataSource,
    private val loader: StudyDataLoader,
    private val catalog: CourseCatalog
) : CourseRepository {

    private val errorMapper: (Throwable) -> String = { it.toFriendlyMessage("No se pudieron cargar los cursos") }

    override suspend fun getAllCourses(): Resource<List<CourseInfo>> = safeApiCall(errorMapper) {
        catalog.all(forceRefresh = true)
    }

    override suspend fun getMyCourses(): Resource<List<CourseInfo>> = safeApiCall(errorMapper) {
        loader.myCourses()
    }

    override suspend fun enroll(courseId: Long): Resource<Unit> = safeApiCall(
        errorMapper = { it.toFriendlyMessage("No se pudo inscribir en el curso") }
    ) {
        dataSource.enroll(courseId, loader.requireUserId())
        loader.invalidate()
        Unit
    }
}