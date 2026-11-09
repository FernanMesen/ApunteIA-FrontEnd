package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.CourseDataSource
import ApunteIA.FrontEnd.data.datasource.model.CourseRequestDto
import ApunteIA.FrontEnd.data.mapper.toDomain
import ApunteIA.FrontEnd.util.CustomSubjectStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectEnroller @Inject constructor(
    private val dataSource: CourseDataSource,
    private val catalog: CourseCatalog,
    private val loader: StudyDataLoader,
    private val customSubjectStore: CustomSubjectStore
) {
    suspend fun add(name: String, code: String, description: String): Boolean {
        val cleanName = name.trim()
        val cleanCode = code.trim()
        if (cleanName.isEmpty()) return false

        val known = runCatching { catalog.all(forceRefresh = true) }.getOrDefault(emptyList())
        val existing = known.firstOrNull {
            it.name.equals(cleanName, ignoreCase = true) ||
                    (cleanCode.isNotEmpty() && it.code.equals(cleanCode, ignoreCase = true))
        }
        val course = existing ?: runCatching {
            dataSource.createCourse(
                CourseRequestDto(
                    code = cleanCode.ifBlank { "SUB-${System.currentTimeMillis() % 100000}" },
                    name = cleanName,
                    description = description.trim().ifBlank { null },
                    syllabus = null
                )
            ).toDomain()
        }.getOrNull()

        if (course == null) {
            customSubjectStore.addCustomCourse(cleanName, cleanCode, description)
            return false
        }

        val alreadyEnrolled = runCatching { loader.myCourses().any { it.id == course.id } }.getOrDefault(false)
        val enrolled = alreadyEnrolled ||
                runCatching { dataSource.enroll(course.id, loader.requireUserId()) }.isSuccess
        if (!enrolled) customSubjectStore.addCustomCourse(cleanName, cleanCode, description)

        loader.invalidate()
        runCatching { catalog.all(forceRefresh = true) }
        return enrolled
    }
}