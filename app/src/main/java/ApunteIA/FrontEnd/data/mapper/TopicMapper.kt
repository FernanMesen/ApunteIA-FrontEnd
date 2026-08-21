package ApunteIA.FrontEnd.data.mapper

import ApunteIA.FrontEnd.data.datasource.model.TopicDto
import ApunteIA.FrontEnd.domain.model.Topic

fun TopicDto.toDomain(): Topic = Topic(
    id = id,
    title = title,
    className = className,
    status = status.toTopicStatus(),
    progress = progress
)

fun List<TopicDto>.toDomain(): List<Topic> = map { it.toDomain() }