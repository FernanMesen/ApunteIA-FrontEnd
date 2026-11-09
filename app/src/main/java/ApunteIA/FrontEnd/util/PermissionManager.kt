package ApunteIA.FrontEnd.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context?
) {
    private val prefs = context?.getSharedPreferences("apunteia_permissions", Context.MODE_PRIVATE)

    private val _cameraMode = MutableStateFlow(
        prefs?.getString("camera_mode", null) ?: "NOT_CONFIGURED"
    )
    val cameraMode: StateFlow<String> = _cameraMode.asStateFlow()

    private val _photosMode = MutableStateFlow(
        prefs?.getString("photos_mode", null) ?: "NOT_CONFIGURED"
    )
    val photosMode: StateFlow<String> = _photosMode.asStateFlow()

    fun updateCameraMode(mode: String) {
        _cameraMode.value = mode
        prefs?.edit()?.putString("camera_mode", mode)?.apply()
    }

    fun updatePhotosMode(mode: String) {
        _photosMode.value = mode
        prefs?.edit()?.putString("photos_mode", mode)?.apply()
    }
}
