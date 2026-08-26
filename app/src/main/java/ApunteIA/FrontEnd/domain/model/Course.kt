package ApunteIA.FrontEnd.domain.model

data class Course(
    val name: String,
    val professor: String,
    val term: String,
    val progress: Int,
    val colorHex: String
)
