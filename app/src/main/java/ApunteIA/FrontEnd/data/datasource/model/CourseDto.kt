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
    val description: String?,
    /** Plan de estudios: un tema por línea (null si el curso no tiene). */
    @SerializedName("syllabus")
    val syllabus: String? = null,
    @SerializedName("createdById")
    val createdById: Long? = null,
    @SerializedName("createdByName")
    val createdByName: String? = null,
    @SerializedName("studentCount")
    val studentCount: Int = 0
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
    val enrolledAt: String?,
    @SerializedName("userFullName")
    val userFullName: String? = null,
    @SerializedName("userEmail")
    val userEmail: String? = null
)

data class EnrollUserRequestDto(
    @SerializedName("userId")
    val userId: Long
)
