package ApunteIA.FrontEnd.data.datasource.model

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

/** Respuesta de POST /auth/login (JWT firmado por el backend). */
data class LoginResponseDto(
    @SerializedName("token")
    val token: String,
    @SerializedName("userName")
    val userName: String,
    @SerializedName("tokenType")
    val tokenType: String? = null,
    @SerializedName("expiresIn")
    val expiresIn: Long? = null,
    @SerializedName("userId")
    val userId: Long? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("role")
    val role: String? = null
)

data class RegisterRequestDto(
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class UserDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("email")
    val email: String?,
    @SerializedName("fullName")
    val fullName: String?,
    @SerializedName("role")
    val role: String?,
    @SerializedName("status")
    val status: String?
)