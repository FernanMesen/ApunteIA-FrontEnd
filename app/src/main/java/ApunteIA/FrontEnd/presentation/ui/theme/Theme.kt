package ApunteIA.FrontEnd.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SpaceCadet,
    onPrimary = Color.White,
    background = DarkThemeBg,
    surface = DarkThemeSurface,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkThemeCard,
    onSurfaceVariant = DarkTextSecondary,
    secondary = Color(0xFF394867)
)

private val LightColorScheme = lightColorScheme(
    primary = DarkGreen,
    onPrimary = Color.White,
    background = BackgroundBeige,
    surface = Color.White,
    onBackground = TextDark,
    onSurface = TextDark,
    surfaceVariant = Color.White,
    onSurfaceVariant = TextGray
)

@Composable
fun ApunteIAFrontendTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Desactivado
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
