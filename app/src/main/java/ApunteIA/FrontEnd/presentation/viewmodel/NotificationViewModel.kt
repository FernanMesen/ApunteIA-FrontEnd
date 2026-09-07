package ApunteIA.FrontEnd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import ApunteIA.FrontEnd.domain.model.Notification
import ApunteIA.FrontEnd.domain.model.NotificationType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor() : ViewModel() {
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    init {
        _notifications.value = listOf(
            Notification(1, "Hora de repasar Estereoquímica — 3 días desde tu última práctica.", "Hace 1h", NotificationType.STUDY_REMINDER),
            Notification(2, "Nueva brecha detectada: Aromaticidad y Benceno sin revisar.", "Hace 5h", NotificationType.ALERT),
            Notification(3, "Examen parcial de Química Orgánica en 6 días.", "Hace 2 dias", NotificationType.ALERT),
            Notification(4, "Tu escaneo de Mecanismos de Reacción terminó de procesarse.", "Ayer", NotificationType.INFO),
            Notification(5, "¡Llevas una racha de 12 días — sigue así!", "Hace 2 dias", NotificationType.SUCCESS),
            Notification(6, "Nuevas tarjetas listas para Transporte en la Membrana Celular.", "Hace 4 dias", NotificationType.INFO)
        )
    }

    fun markAllAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun deleteNotification(id: Int) {
        _notifications.value = _notifications.value.filter { it.id != id }
    }
}
