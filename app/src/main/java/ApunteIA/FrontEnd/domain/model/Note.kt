package ApunteIA.FrontEnd.domain.model

/**
 * Apunte del estudiante guardado en el backend. El [summary] lo genera la IA
 * (POST /notes/{id}/process) y [processingStatus] indica en qué va ese proceso.
 */
data class Note(
    val id: Int,
    val title: String,
    val className: String,
    val classNumber: Int,
    val date: String,
    val preview: String,
    val status: NoteStatus,
    val courseId: Long = 0L,
    val content: String = "",
    val summary: String? = null,
    val processingStatus: ProcessingStatus = ProcessingStatus.PENDING,
    /** Fecha ISO original del backend, útil para ordenar y para la actividad reciente. */
    val createdAtIso: String? = null
)

/** Estado del resumen con IA de una nota (mismo enum que el backend). */
enum class ProcessingStatus(val label: String) {
    PENDING("Sin resumir"),
    PROCESSING("Procesando"),
    COMPLETED("Resumida con IA"),
    FAILED("Falló la IA");

    companion object {
        fun fromApi(value: String?): ProcessingStatus =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: PENDING
    }
}