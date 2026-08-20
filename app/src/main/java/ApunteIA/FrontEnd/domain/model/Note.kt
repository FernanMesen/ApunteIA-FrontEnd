package ApunteIA.FrontEnd.domain.model

/**
 * Representa un apunte generado/estructurado por la IA a partir de un escaneo.
 */
data class Note(
    val id: Int,
    val title: String,
    val className: String,
    val classNumber: Int,
    val date: String,
    val preview: String,
    val status: NoteStatus
)
