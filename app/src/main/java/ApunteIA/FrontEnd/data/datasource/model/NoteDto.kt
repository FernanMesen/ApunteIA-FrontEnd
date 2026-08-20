package ApunteIA.FrontEnd.data.datasource.model

/**
 * Representación "cruda" de una nota tal como llegaría de una fuente de
 * datos (API, base de datos local, etc.). A propósito usa `status` como
 * `String` en vez del enum de dominio: así el [ApunteIA.FrontEnd.data.mapper.NoteMapper]
 * tiene un motivo real para existir, igual que pasaría con un JSON real.
 */
data class NoteDto(
    val id: Int,
    val title: String,
    val className: String,
    val classNumber: Int,
    val date: String,
    val preview: String,
    val status: String
)
