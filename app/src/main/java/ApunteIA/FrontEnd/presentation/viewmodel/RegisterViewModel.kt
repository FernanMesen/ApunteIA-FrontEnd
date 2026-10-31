package ApunteIA.FrontEnd.presentation.viewmodel

import ApunteIA.FrontEnd.domain.usecase.RegisterUseCase
import ApunteIA.FrontEnd.domain.util.Resource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val registered: Boolean = false
)

/** Crea la cuenta en POST /auth/register y deja la sesión iniciada (JWT). */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(fullName: String, email: String, password: String) {
        val validationError = when {
            fullName.isBlank() -> "Ingresa tu nombre completo."
            !EMAIL_REGEX.matches(email.trim()) -> "Ingresa un correo válido."
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres."
            password.length > 72 -> "La contraseña no puede tener más de 72 caracteres."
            else -> null
        }
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = registerUseCase(fullName, email, password)) {
                is Resource.Success -> _uiState.update { it.copy(isLoading = false, registered = true) }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun onRegisterHandled() {
        _uiState.update { it.copy(registered = false) }
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    }
}