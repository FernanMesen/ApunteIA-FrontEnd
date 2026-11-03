package ApunteIA.FrontEnd.data.mapper

import ApunteIA.FrontEnd.data.datasource.model.CourseDto
import ApunteIA.FrontEnd.domain.model.CourseInfo

fun CourseDto.toDomain(): CourseInfo = CourseInfo(
    id = id,
    code = code.orEmpty(),
    name = name?.takeIf { it.isNotBlank() } ?: code.orEmpty().ifBlank { "Curso $id" },
    description = description.orEmpty(),
    syllabus = syllabus.orEmpty()
)

fun List<CourseDto>.toDomain(): List<CourseInfo> = map { it.toDomain() }
