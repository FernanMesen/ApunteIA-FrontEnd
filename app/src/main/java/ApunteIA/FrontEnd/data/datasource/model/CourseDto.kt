package ApunteIA.FrontEnd.data.datasource.model

import com.google.gson.annotations.SerializedName

data class CourseDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("code")
    val code: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("description")
    val description: String?
)

data class EnrollmentDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("userId")
    val userId: Long?,
    @SerializedName("courseId")
    val courseId: Long,
    @SerializedName("courseCode")
    val courseCode: String?,
    @SerializedName("courseName")
    val courseName: String?,
    @SerializedName("enrolledAt")
    val enrolledAt: String?
)

data class EnrollUserRequestDto(
    @SerializedName("userId")
    val userId: Long
)