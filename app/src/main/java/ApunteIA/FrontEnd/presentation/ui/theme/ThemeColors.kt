package ApunteIA.FrontEnd.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

/*
 * Ayudas para que las pantallas con colores fijos (onboarding, escaneo, quiz) se vean
 * bien en modo oscuro SIN cambiar el modo claro: en claro se usa exactamente el color
 * que la pantalla tenía; en oscuro, el equivalente del tema.
 */

/** Tonos del modo oscuro que ya usa el resto de la app (Inicio, Perfil, Reforzar...). */
val DarkButton = Color(0xFF394867)      // botones y elementos seleccionados con fondo
val DarkBorder = Color(0xFF394867)      // bordes de tarjetas y campos
val DarkAccent = Color(0xFF8FB2C9)      // íconos, textos de acción y barras de progreso
val DarkSoftFill = Color(0xFF252B3B)    // fondos suaves (botón cerrar, chips, insignias)
val DarkDisabled = Color(0xFF4A5568)    // íconos/bordes deshabilitados

/** Patrón del proyecto para saber si está activo el modo oscuro. */
@Composable
@ReadOnlyComposable
fun isDarkTheme(): Boolean = MaterialTheme.colorScheme.primary == SpaceCadet

/** [light] en modo claro (sin cambios) y [dark] en modo oscuro. */
@Composable
@ReadOnlyComposable
fun lightOr(light: Color, dark: Color): Color = if (isDarkTheme()) dark else light
