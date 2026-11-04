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

/** Pestaña que el usuario elige en el Login; debe coincidir con el rol que devuelve el backend. */
enum class LoginRole {
    STUDENT,
    ADMIN;

    companion object {
        /** El backend manda "ADMIN" o "STUDENT"; cualquier otro valor se trata como estudiante. */
        fun fromBackendRole(role: String): LoginRole =
            if (role.equals("ADMIN", ignoreCase = true)) ADMIN else STUDENT
    }
}