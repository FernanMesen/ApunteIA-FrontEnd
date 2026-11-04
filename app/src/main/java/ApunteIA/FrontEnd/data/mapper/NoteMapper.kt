package ApunteIA.FrontEnd.data.mapper

import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.data.datasource.model.NoteDto
import ApunteIA.FrontEnd.domain.model.CourseInfo
import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.model.NoteStatus
import ApunteIA.FrontEnd.domain.model.ProcessingStatus
import ApunteIA.FrontEnd.domain.util.IsoDates
import ApunteIA.FrontEnd.util.L10n

private const val PREVIEW_LENGTH = 160

/**
 * Traduce la `NoteResponse` del backend a la entidad de dominio [Note].
 * El backend no tiene "número de clase": se calcula como la posición de la nota
 * dentro de su curso, ordenando por fecha de creación.
 */
fun NoteDto.toDomain(
    courses: Map<Long, CourseInfo>,
    classNumber: Int = 0,
    status: NoteStatus = NoteStatus.WEAK
): Note {
    val course = courseId?.let { courses[it] }
    val text = summary?.takeIf { it.isNotBlank() } ?: content.orEmpty()
    return Note(
        id = id.toInt(),
        title = title.orEmpty().ifBlank { L10n.getString(R.string.fallback_untitled_note) },
        className = course?.name ?: courseCode.orEmpty().ifBlank { L10n.getString(R.string.fallback_no_course) },
        classNumber = classNumber,
        date = IsoDates.shortDate(createdAt),
        preview = text.replace(Regex("\\s+"), " ").trim().take(PREVIEW_LENGTH),
        status = status,
        courseId = courseId ?: 0L,
        content = content.orEmpty(),
        summary = summary?.takeIf { it.isNotBlank() },
        processingStatus = ProcessingStatus.fromApi(processingStatus),
        createdAtIso = createdAt
    )
}

/** Mapea la lista completa calculando el número de clase por curso. Devuelve las más recientes primero. */
fun List<NoteDto>.toDomain(
    courses: Map<Long, CourseInfo>,
    statusOf: (Note) -> NoteStatus = { NoteStatus.WEAK }
): List<Note> {
    val classNumbers = HashMap<Long, Int>()
    groupBy { it.courseId ?: 0L }.values.forEach { inCourse ->
        inCourse
            .sortedWith(compareBy<NoteDto>({ IsoDates.parse(it.createdAt) ?: 0L }, { it.id }))
            .forEachIndexed { index, dto -> classNumbers[dto.id] = index + 1 }
    }
    return map { dto ->
        val note = dto.toDomain(courses, classNumbers[dto.id] ?: 0)
        note.copy(status = statusOf(note))
    }.sortedByDescending { IsoDates.parse(it.createdAtIso) ?: it.id.toLong() }
}
