package ApunteIA.FrontEnd.presentation.viewmodel

import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.domain.error.AuthError
import ApunteIA.FrontEnd.domain.usecase.LoginUseCase
import ApunteIA.FrontEnd.domain.util.Resource
import ApunteIA.FrontEnd.util.L10n
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val authError: AuthError? = null,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false
)

/**
 * Maneja el estado de la pantalla de Login.
 * Llama a [LoginUseCase], que a su vez llama a AuthRepository -> ApiService (Mockoon).
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String, selectedRole: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                authError = null,
                errorMessage = L10n.getString(R.string.login_error_empty)
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, authError = null, errorMessage = null)

            when (val result = loginUseCase(email.trim(), password, selectedRole)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        loginSuccess = true
                    )
                }
                is Resource.Error -> {
                    val err = result.throwable as? AuthError
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        authError = err,
                        errorMessage = if (err == null) result.message else null
                    )
                }
                is Resource.Loading -> Unit
            }
        }
    }

    /** Se llama después de navegar, para no volver a disparar la navegación. */
    fun onLoginHandled() {
        _uiState.value = _uiState.value.copy(loginSuccess = false)
    }
}
