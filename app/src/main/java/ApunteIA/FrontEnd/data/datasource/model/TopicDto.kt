package ApunteIA.FrontEnd.data.datasource.model

import com.google.gson.annotations.SerializedName

data class TopicDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("className")
    val className: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("progress")
    val progress: Int
)