package ApunteIA.FrontEnd.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import ApunteIA.FrontEnd.presentation.viewmodel.CurrentUser

/** Usuario con sesión iniciada, disponible para el encabezado y el perfil. */
val LocalCurrentUser = staticCompositionLocalOf<CurrentUser?> { null }

/**
 * Ejecuta [onResume] cada vez que la pantalla vuelve a mostrarse (al entrar y al
 * regresar desde otra pantalla), para que los datos del backend no queden viejos.
 */
@Composable
fun RefreshOnResume(onResume: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnResume by rememberUpdatedState(onResume)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) currentOnResume()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}