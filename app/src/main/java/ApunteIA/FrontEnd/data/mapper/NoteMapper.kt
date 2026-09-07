package ApunteIA.FrontEnd.data.mapper

import ApunteIA.FrontEnd.data.datasource.model.NoteDto
import ApunteIA.FrontEnd.domain.model.Note

/**
 * Traduce entre el modelo de la capa de datos ([NoteDto]) y la entidad de
 * dominio ([Note]). El `status` se delega a [toNoteStatus].
 */
fun NoteDto.toDomain(): Note = Note(
    id = id?.toIntOrNull() ?: 0,
    title = title,
    className = className,
    classNumber = classNumber,
    date = date,
    preview = preview,
    status = status.toNoteStatus()
)

fun List<NoteDto>.toDomain(): List<Note> = map { it.toDomain() }

/**
 * Traduce la entidad de dominio ([Note]) de vuelta al modelo de la capa de
 * datos ([NoteDto]), necesario al guardar una nota nueva (p.ej. desde el
 * flujo de escaneo) hacia la fuente de datos.
 */
fun Note.toDto(): NoteDto = NoteDto(
    id = id.toString(),
    title = title,
    className = className,
    classNumber = classNumber,
    date = date,
    preview = preview,
    status = status.label
)
