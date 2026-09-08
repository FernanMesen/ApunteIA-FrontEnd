package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.error.AuthError
import ApunteIA.FrontEnd.domain.model.AuthSession
import ApunteIA.FrontEnd.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<AuthSession> {
        return try {
            Result.success(repository.login(email, password))
        } catch (e: Exception) {
            Result.failure(e as? AuthError ?: AuthError.Unknown)
        }
    }
}