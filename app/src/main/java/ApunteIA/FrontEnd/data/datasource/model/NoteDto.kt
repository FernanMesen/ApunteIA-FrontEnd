package ApunteIA.FrontEnd.data.datasource.model

import com.google.gson.annotations.SerializedName

data class NoteDto(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("title")
    val title: String,
    @SerializedName("className")
    val className: String,
    @SerializedName("classNumber")
    val classNumber: Int,
    @SerializedName("date")
    val date: String,
    @SerializedName("preview")
    val preview: String,
    @SerializedName("status")
    val status: String
)