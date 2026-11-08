package ApunteIA.FrontEnd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.data.datasource.model.ForgotPasswordRequestDto
import ApunteIA.FrontEnd.data.remote.ApiService
import ApunteIA.FrontEnd.data.util.toFriendlyMessage
import ApunteIA.FrontEnd.util.L10n
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

data class ForgotPasswordUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val message: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun sendRecoveryEmail(email: String) {
        viewModelScope.launch {
            _uiState.value = ForgotPasswordUiState(isLoading = true)
            try {
                val response = apiService.forgotPassword(ForgotPasswordRequestDto(email))
                if (response.isSuccessful) {
                    _uiState.value = ForgotPasswordUiState(
                        isLoading = false,
                        isSuccess = true,
                        message = L10n.getString(R.string.forgot_password_success_neutral)
                    )
                } else {
                    if (response.code() >= 500) {
                        _uiState.value = ForgotPasswordUiState(
                            isLoading = false,
                            errorMessage = L10n.getString(R.string.api_error_unknown)
                        )
                    } else {
                        // Respuesta pública genérica para evitar enumeración de correos
                        _uiState.value = ForgotPasswordUiState(
                            isLoading = false,
                            isSuccess = true,
                            message = L10n.getString(R.string.forgot_password_success_neutral)
                        )
                    }
                }
            } catch (e: IOException) {
                _uiState.value = ForgotPasswordUiState(
                    isLoading = false,
                    errorMessage = e.toFriendlyMessage(L10n.getString(R.string.auth_error_connection))
                )
            } catch (e: Exception) {
                _uiState.value = ForgotPasswordUiState(
                    isLoading = false,
                    errorMessage = e.toFriendlyMessage(L10n.getString(R.string.api_error_unknown))
                )
            }
        }
    }

    fun setError(message: String) {
        _uiState.value = _uiState.value.copy(errorMessage = message)
    }
}
