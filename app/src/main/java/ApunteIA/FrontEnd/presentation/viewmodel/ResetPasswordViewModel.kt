package ApunteIA.FrontEnd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ApunteIA.FrontEnd.data.datasource.model.ResetPasswordRequestDto
import ApunteIA.FrontEnd.data.remote.ApiService
import ApunteIA.FrontEnd.data.util.toFriendlyMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResetPasswordUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun resetPassword(token: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.value = ResetPasswordUiState(isLoading = true)
            try {
                val response = apiService.resetPassword(ResetPasswordRequestDto(token, newPassword))
                if (response.isSuccessful || response.code() == 204) {
                    _uiState.value = ResetPasswordUiState(isLoading = false, isSuccess = true)
                } else {
                    _uiState.value = ResetPasswordUiState(
                        isLoading = false,
                        errorMessage = "Token vencido o inválido. Solicita un nuevo enlace."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = ResetPasswordUiState(
                    isLoading = false,
                    errorMessage = e.toFriendlyMessage("Token vencido o inválido.")
                )
            }
        }
    }

    fun setError(message: String) {
        _uiState.value = _uiState.value.copy(errorMessage = message)
    }
}
