package ApunteIA.FrontEnd.data.datasource.model

import com.google.gson.annotations.SerializedName

/** Cuerpo de POST /courses y PUT /courses/{id}. POST permite estudiantes; PUT requiere creador/ADMIN. */
data class CourseRequestDto(
    @SerializedName("code")
    val code: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("syllabus")
    val syllabus: String?
)
