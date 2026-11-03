package ApunteIA.FrontEnd.domain.model

data class Course(
    val name: String,
    val professor: String,
    val term: String,
    val progress: Int,
    val colorHex: String,
    /** Temas del plan de estudios (vacío si el administrador no lo ha cargado). */
    val syllabus: List<String> = emptyList()
)
