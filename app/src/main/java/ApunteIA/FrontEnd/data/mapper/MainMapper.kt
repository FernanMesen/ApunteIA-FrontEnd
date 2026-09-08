package ApunteIA.FrontEnd.data.mapper

import ApunteIA.FrontEnd.data.datasource.model.RecentActivityDto
import ApunteIA.FrontEnd.data.datasource.model.UserStatsDto
import ApunteIA.FrontEnd.domain.model.RecentActivity
import ApunteIA.FrontEnd.domain.model.UserStats

fun UserStatsDto.toDomain(): UserStats = UserStats(
    userName = userName,
    streakDays = streakDays,
    progressPercent = progressPercent,
    notesCount = notesCount,
    pendingTopicsCount = pendingTopicsCount,
    quizzesDoneCount = quizzesDoneCount
)

fun RecentActivityDto.toDomain(): RecentActivity = RecentActivity(
    title = title,
    subtitle = subtitle,
    time = time
)

fun List<RecentActivityDto>.toDomain(): List<RecentActivity> = map { it.toDomain() }