package ApunteIA.FrontEnd.domain.model

/**
 * Nivel de dominio del estudiante sobre una nota, calculado por la IA
 * a partir de los cuestionarios y repasos realizados.
 */
enum class NoteStatus(val label: String) {
    WEAK("Débil"),
    SOLID("Sólido");

    companion object {
        fun fromLabel(label: String): NoteStatus =
            entries.firstOrNull { it.label == label } ?: SOLID
    }
}
