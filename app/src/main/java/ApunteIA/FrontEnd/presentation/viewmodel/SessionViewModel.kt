package ApunteIA.FrontEnd.presentation.viewmodel

import ApunteIA.FrontEnd.domain.model.AuthSession
import ApunteIA.FrontEnd.domain.repository.AuthRepository
import ApunteIA.FrontEnd.domain.usecase.GetCurrentSessionUseCase
import ApunteIA.FrontEnd.domain.usecase.LogoutUseCase
import ApunteIA.FrontEnd.domain.usecase.WarmUpBackendUseCase
import ApunteIA.FrontEnd.domain.util.Resource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Usuario que se muestra en el encabezado y el perfil. */
data class CurrentUser(
    val name: String,
    val email: String,
    /** true si el backend devolvió el rol ADMIN en el login. */
    val isAdmin: Boolean = false
) {
    val initial: String get() = name.trim().firstOrNull()?.uppercase() ?: "?"
}

/**
 * Estado de sesión a nivel de app: decide la pantalla inicial (login o inicio),
 * expone el usuario actual y avisa cuando el backend invalida el token.
 */
@HiltViewModel
class SessionViewModel @Inject constructor(
    private val getCurrentSession: GetCurrentSessionUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val warmUpBackend: WarmUpBackendUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    /** Se calcula una sola vez al abrir la app. */
    val startLoggedIn: Boolean = getCurrentSession() != null

    private val _currentUser = MutableStateFlow(getCurrentSession()?.toUser())
    val currentUser: StateFlow<CurrentUser?> = _currentUser.asStateFlow()

    val sessionExpired: Flow<Unit> = getCurrentSession.sessionExpired

    init {
        // Render (plan gratuito) se duerme: se despierta mientras el usuario escribe.
        viewModelScope.launch { warmUpBackend() }
    }

    fun refreshUser() {
        _currentUser.value = getCurrentSession()?.toUser()
    }

    fun logout() {
        logoutUseCase()
        _currentUser.value = null
    }

    suspend fun deleteAccount(): Resource<Unit> {
        val result = authRepository.deleteAccount()
        if (result is Resource.Success) {
            logout()
        }
        return result
    }

    private fun AuthSession.toUser() = CurrentUser(
        name = userName,
        email = email,
        isAdmin = role.equals("ADMIN", ignoreCase = true)
    )
}
