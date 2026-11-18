package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.data.datasource.CourseDataSource
import ApunteIA.FrontEnd.data.datasource.model.CourseRequestDto
import ApunteIA.FrontEnd.domain.model.CourseInfo
import ApunteIA.FrontEnd.domain.usecase.CreateCourseUseCase
import ApunteIA.FrontEnd.domain.util.Resource
import ApunteIA.FrontEnd.util.L10n
import retrofit2.HttpException
import kotlin.coroutines.cancellation.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectEnroller @Inject constructor(
    private val dataSource: CourseDataSource,
    private val catalog: CourseCatalog,
    private val loader: StudyDataLoader,
    private val createCourseUseCase: CreateCourseUseCase
) {
    suspend fun add(name: String, code: String, description: String): Resource<CourseInfo> {
        val cleanName = name.trim()
        val cleanCode = CreateCourseUseCase.normalizeCode(code)
        if (cleanName.isEmpty()) return Resource.Error(L10n.get(R.string.course_name_required))

        val known = try {
            catalog.all(forceRefresh = true)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (t: Throwable) {
            return Resource.Error(t.message ?: L10n.get(R.string.error_loading_courses), t)
        }

        val matches = if (cleanCode.isNotEmpty()) {
            known.filter { CreateCourseUseCase.normalizeCode(it.code) == cleanCode }
        } else {
            known.filter { it.name.trim().equals(cleanName, ignoreCase = true) }
        }

        if (matches.size > 1) {
            return Resource.Error(L10n.get(R.string.course_ambiguous_name))
        }

        val course = matches.firstOrNull()
        if (course == null) {
            if (cleanCode.isNotEmpty()) return Resource.Error(L10n.get(R.string.error_course_not_found, cleanCode))
            val created = createCourseUseCase(cleanName, "", description)
            if (created is Resource.Success) {
                loader.invalidate()
                try { catalog.all(forceRefresh = true) } catch (_: Throwable) {}
            }
            return created
        }

        val mine = try {
            loader.myCourses()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (t: Throwable) {
            return Resource.Error(t.message ?: L10n.get(R.string.error_loading_courses), t)
        }
        if (mine.any { it.id == course.id }) {
            return Resource.Success(course)
        }

        return try {
            dataSource.enroll(course.id, loader.requireUserId())
            loader.invalidate()
            try { catalog.all(forceRefresh = true) } catch (_: Throwable) {}
            Resource.Success(course)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (e: HttpException) {
            if (e.code() == 409) {
                val confirmed = try { loader.myCourses().any { it.id == course.id } } catch (_: Throwable) { false }
                if (confirmed) {
                    loader.invalidate()
                    return Resource.Success(course)
                }
            }
            Resource.Error(e.message ?: L10n.get(R.string.error_enroll_course), e)
        } catch (t: Throwable) {
            Resource.Error(t.message ?: L10n.get(R.string.error_enroll_course), t)
        }
    }
}