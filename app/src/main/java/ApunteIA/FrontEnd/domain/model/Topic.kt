package ApunteIA.FrontEnd.domain.model

data class Topic(
    val id: Int,
    val title: String,
    val className: String,
    val status: TopicStatus,
    val progress: Int // 0..100
)