package ApunteIA.FrontEnd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ApunteIA.FrontEnd.domain.model.Notification
import ApunteIA.FrontEnd.domain.model.NotificationType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationUiState(
    val isLoading: Boolean = false,
    val notifications: List<Notification> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class NotificationViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    // Backwards compatibility for callers observing notification flow directly
    val notifications: StateFlow<List<Notification>> get() = MutableStateFlow(_uiState.value.notifications).asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            delay(200) // Simular latencia de red al obtener notificaciones
            val defaultNotifications = listOf(
                Notification(1, "Hora de repasar Estereoquímica — 3 días desde tu última práctica.", "Hace 1h", NotificationType.STUDY_REMINDER),
                Notification(2, "Nueva brecha detectada: Aromaticidad y Benceno sin revisar.", "Hace 5h", NotificationType.ALERT),
                Notification(3, "Examen parcial de Química Orgánica en 6 días.", "Hace 2 dias", NotificationType.ALERT),
                Notification(4, "Tu escaneo de Mecanismos de Reacción terminó de procesarse.", "Ayer", NotificationType.INFO),
                Notification(5, "¡Llevas una racha de 12 días — sigue así!", "Hace 2 dias", NotificationType.SUCCESS),
                Notification(6, "Nuevas tarjetas listas para Transporte en la Membrana Celular.", "Hace 4 dias", NotificationType.INFO)
            )
            _uiState.value = NotificationUiState(
                isLoading = false,
                notifications = defaultNotifications,
                error = null
            )
        }
    }

    fun refresh() {
        loadNotifications()
    }

    fun markAllAsRead() {
        val updated = _uiState.value.notifications.map { it.copy(isRead = true) }
        _uiState.value = _uiState.value.copy(notifications = updated)
    }

    fun deleteNotification(id: Int) {
        val updated = _uiState.value.notifications.filter { it.id != id }
        _uiState.value = _uiState.value.copy(notifications = updated)
    }
}
