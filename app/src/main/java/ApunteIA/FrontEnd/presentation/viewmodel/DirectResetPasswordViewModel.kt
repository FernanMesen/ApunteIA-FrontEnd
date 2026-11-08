package ApunteIA.FrontEnd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ApunteIA.FrontEnd.data.datasource.model.DirectResetPasswordRequestDto
import ApunteIA.FrontEnd.data.remote.ApiService
import ApunteIA.FrontEnd.data.util.toFriendlyMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DirectResetPasswordUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class DirectResetPasswordViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(DirectResetPasswordUiState())
    val uiState: StateFlow<DirectResetPasswordUiState> = _uiState.asStateFlow()

    fun directResetPassword(email: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.value = DirectResetPasswordUiState(isLoading = true)
            try {
                val response = apiService.directResetPassword(DirectResetPasswordRequestDto(email, newPassword))
                if (response.isSuccessful || response.code() == 204) {
                    _uiState.value = DirectResetPasswordUiState(isLoading = false, isSuccess = true)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = if (!errorBody.isNullOrBlank()) {
                        try {
                            org.json.JSONObject(errorBody).optString("message").takeIf { it.isNotBlank() }
                        } catch (_: Exception) {
                            null
                        }
                    } else null
                    _uiState.value = DirectResetPasswordUiState(
                        isLoading = false,
                        errorMessage = errorMsg ?: "Error al actualizar la contraseña (${response.code()})"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = DirectResetPasswordUiState(
                    isLoading = false,
                    errorMessage = e.toFriendlyMessage("Error al conectar con el servidor")
                )
            }
        }
    }

    fun setError(message: String) {
        _uiState.value = _uiState.value.copy(errorMessage = message)
    }
}
