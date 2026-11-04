package ApunteIA.FrontEnd.data.datasource.model

import com.google.gson.annotations.SerializedName

/** NoteResponse del backend (GET/POST/PUT /notes). */
data class NoteDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("summary")
    val summary: String?,
    @SerializedName("userId")
    val userId: Long?,
    @SerializedName("courseId")
    val courseId: Long?,
    @SerializedName("courseCode")
    val courseCode: String?,
    /** PENDING | PROCESSING | COMPLETED | FAILED */
    @SerializedName("processingStatus")
    val processingStatus: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    /** Fecha del apunte "yyyy-MM-dd" (puede ser null en notas viejas). */
    @SerializedName("noteDate")
    val noteDate: String? = null
)

data class CreateNoteRequestDto(
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("courseId")
    val courseId: Long,
    @SerializedName("noteDate")
    val noteDate: String? = null
)

data class UpdateNoteRequestDto(
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("courseId")
    val courseId: Long,
    @SerializedName("summary")
    val summary: String? = null,
    @SerializedName("noteDate")
    val noteDate: String? = null
)