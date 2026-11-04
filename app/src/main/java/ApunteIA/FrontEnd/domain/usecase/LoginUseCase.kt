package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.domain.model.AuthSession
import ApunteIA.FrontEnd.domain.model.LoginRole
import ApunteIA.FrontEnd.domain.repository.AuthRepository
import ApunteIA.FrontEnd.domain.util.Resource
import ApunteIA.FrontEnd.util.L10n
import javax.inject.Inject

/**
 * Inicia sesión y verifica que el rol de la cuenta coincida con la pestaña elegida
 * (Estudiante / Administrador). Si no coincide, cierra la sesión recién guardada.
 */
class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        expectedRole: LoginRole = LoginRole.STUDENT
    ): Resource<AuthSession> {
        val result = repository.login(email, password)
        if (result !is Resource.Success) return result

        if (LoginRole.fromBackendRole(result.data.role) == expectedRole) return result

        repository.logout()
        val message = when (expectedRole) {
            LoginRole.ADMIN -> R.string.login_error_not_admin
            LoginRole.STUDENT -> R.string.login_error_is_admin
        }
        return Resource.Error(L10n.getString(message))
    }
}
