package ApunteIA.FrontEnd.domain.model

data class AuthSession(
    val token: String,
    val userName: String,
    val userId: Long = 0L,
    val email: String = "",
    val role: String = "",
    /** Segundos de vida del JWT según el backend (0 = desconocido). */
    val expiresInSeconds: Long = 0L
)