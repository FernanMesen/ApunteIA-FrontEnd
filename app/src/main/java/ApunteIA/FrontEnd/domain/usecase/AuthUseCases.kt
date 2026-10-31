package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.model.AuthSession
import ApunteIA.FrontEnd.domain.repository.AuthRepository
import ApunteIA.FrontEnd.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(fullName: String, email: String, password: String): Resource<AuthSession> =
        repository.register(fullName.trim(), email.trim(), password)
}

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke() = repository.logout()
}

class GetCurrentSessionUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): AuthSession? = repository.currentSession()
    val sessionExpired: Flow<Unit> get() = repository.sessionExpired
}

class WarmUpBackendUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.warmUp()
}