package rs.mozaikmozgalica.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF059669),
    secondary = Color(0xFFD97706),
    tertiary = Color(0xFF0284C7)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF059669),
    secondary = Color(0xFFD97706),
    tertiary = Color(0xFF0284C7),
    background = Color(0xFFF8FAFC),
    surface = Color.White
)

@Composable
fun MozaikMozgalicaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
