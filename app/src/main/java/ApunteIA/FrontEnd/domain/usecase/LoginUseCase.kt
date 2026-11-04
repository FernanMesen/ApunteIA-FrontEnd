package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.model.AuthSession
import ApunteIA.FrontEnd.domain.repository.AuthRepository
import ApunteIA.FrontEnd.domain.util.Resource
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, selectedRole: String = "Estudiante"): Resource<AuthSession> {
        return repository.login(email, password, selectedRole)
    }
}
