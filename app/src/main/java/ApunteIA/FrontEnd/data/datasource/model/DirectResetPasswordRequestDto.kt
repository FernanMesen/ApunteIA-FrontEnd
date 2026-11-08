package ApunteIA.FrontEnd.data.datasource.model

data class DirectResetPasswordRequestDto(
    val email: String,
    val newPassword: String
)
