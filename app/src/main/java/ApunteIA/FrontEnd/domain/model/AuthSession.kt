package ApunteIA.FrontEnd.domain.model

data class AuthSession(
    val token: String,
    val userName: String
)