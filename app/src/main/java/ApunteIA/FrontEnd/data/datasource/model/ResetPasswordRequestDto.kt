package ApunteIA.FrontEnd.data.datasource.model

data class ResetPasswordRequestDto(
    val token: String,
    val newPassword: String
)
