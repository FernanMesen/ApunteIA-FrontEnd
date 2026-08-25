package ApunteIA.FrontEnd.domain.model

data class UserStats(
    val userName: String,
    val streakDays: Int,
    val progressPercent: Int,
    val notesCount: Int,
    val pendingTopicsCount: Int,
    val quizzesDoneCount: Int
)
