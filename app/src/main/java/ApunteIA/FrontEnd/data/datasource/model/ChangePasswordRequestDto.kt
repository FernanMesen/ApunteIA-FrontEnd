package ApunteIA.FrontEnd.data.datasource.model

data class ChangePasswordRequestDto(
    val currentPassword: String,
    val newPassword: String
)
