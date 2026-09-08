package ApunteIA.FrontEnd.data.datasource.model

import com.google.gson.annotations.SerializedName

data class UserStatsDto(
    @SerializedName("userName")
    val userName: String,
    @SerializedName("streakDays")
    val streakDays: Int,
    @SerializedName("progressPercent")
    val progressPercent: Int,
    @SerializedName("notesCount")
    val notesCount: Int,
    @SerializedName("pendingTopicsCount")
    val pendingTopicsCount: Int,
    @SerializedName("quizzesDoneCount")
    val quizzesDoneCount: Int
)

data class RecentActivityDto(
    @SerializedName("title")
    val title: String,
    @SerializedName("subtitle")
    val subtitle: String,
    @SerializedName("time")
    val time: String
)