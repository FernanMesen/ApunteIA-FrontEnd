package ApunteIA.FrontEnd.data.datasource.model

//representan la forma "cruda" del dato tal como llegaría de una fuente externa
data class TopicDto(
    val id: Int,
    val title: String,
    val className: String,
    val status: String,
    val progress: Int
)