package ApunteIA.FrontEnd.data.mapper

import ApunteIA.FrontEnd.domain.model.TopicStatus

fun String.toTopicStatus(): TopicStatus = TopicStatus.fromLabel(this)