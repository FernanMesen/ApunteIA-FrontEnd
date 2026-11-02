package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.data.datasource.CourseDataSource
import ApunteIA.FrontEnd.data.util.safeApiCall
import ApunteIA.FrontEnd.data.util.toFriendlyMessage
import ApunteIA.FrontEnd.domain.model.CourseInfo
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
}
