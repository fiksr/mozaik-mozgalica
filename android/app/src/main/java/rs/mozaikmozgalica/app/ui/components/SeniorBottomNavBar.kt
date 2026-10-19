package rs.mozaikmozgalica.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.mozaikmozgalica.app.data.AppState
import rs.mozaikmozgalica.app.data.SerbianTransliteration

@Composable
fun SeniorBottomNavBar(
    currentTab: String,
    appState: AppState,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple("dashboard", "Početna", Icons.Default.Home),
            Triple("games", "Kvizovi", Icons.Default.PlayArrow),
            Triple("stats", "Napredak", Icons.Default.Star),
            Triple("settings", "Opcije", Icons.Default.Settings)
        )

        items.forEach { (route, label, icon) ->
            val selected = currentTab == route
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(route) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (selected) Color(0xFF059669) else Color.Gray
                    )
                },
                label = {
                    Text(
                        text = SerbianTransliteration.applyScript(label, appState.scriptType),
                        fontSize = (10 * appState.fontSize.scale).sp,
                        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                        color = if (selected) Color(0xFF059669) else Color.Gray
                    )
                }
            )
        }
    }
}
