package ApunteIA.FrontEnd.data.mapper

import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.data.datasource.model.CourseDto
import ApunteIA.FrontEnd.domain.model.CourseInfo
import ApunteIA.FrontEnd.util.L10n

fun CourseDto.toDomain(): CourseInfo = CourseInfo(
    id = id,
    code = code.orEmpty(),
    name = name?.takeIf { it.isNotBlank() } ?: code.orEmpty().ifBlank { L10n.getString(R.string.fallback_course_id, id) },
    description = description.orEmpty(),
    syllabus = syllabus.orEmpty(),
    createdById = createdById,
    createdByName = createdByName.orEmpty(),
    studentCount = studentCount.coerceAtLeast(0)
)

fun List<CourseDto>.toDomain(): List<CourseInfo> = map { it.toDomain() }
