package ApunteIA.FrontEnd.domain.model

enum class TopicStatus(val label: String) {
    SOLID("Sólido"),
    WEAK("Débil"),
    MISSING("Faltante");

    companion object {
        fun fromLabel(label: String): TopicStatus =
            entries.firstOrNull { it.label == label } ?: MISSING
    }
}