package ApunteIA.FrontEnd.data.datasource.model

import com.google.gson.annotations.SerializedName

data class QuestionDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("text")
    val text: String,
    @SerializedName("options")
    val options: List<String>,
    @SerializedName("correctAnswerIndex")
    val correctAnswerIndex: Int,
    @SerializedName("explanation")
    val explanation: String
)