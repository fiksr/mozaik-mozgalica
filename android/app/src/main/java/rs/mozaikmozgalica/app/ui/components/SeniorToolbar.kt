package rs.mozaikmozgalica.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.mozaikmozgalica.app.data.AppState
import rs.mozaikmozgalica.app.data.ScriptType
import rs.mozaikmozgalica.app.data.SerbianTransliteration

@Composable
fun SeniorTopToolbar(
    appState: AppState,
    onToggleScript: () -> Unit,
    onToggleFontSize: () -> Unit,
    onToggleSound: () -> Unit,
    onLogoClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding() // Safe padding from clock & battery status bar
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Title & Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.clickable { onLogoClick() }
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF059669), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Logo",
                        tint = Color.White
                    )
                }
                Column {
                    Text(
                        text = "Mozaik Mozgalica",
                        fontSize = (15 * appState.fontSize.scale).sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = SerbianTransliteration.applyScript("Održavajte Vaš Um Zdravim", appState.scriptType),
                        fontSize = (9 * appState.fontSize.scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }

            // Toolbar Accessibility Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Script Toggle
                Button(
                    onClick = onToggleScript,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF3C7), contentColor = Color(0xFF92400E)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = if (appState.scriptType == ScriptType.CIRILICA) "ЋИР" else "LAT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                // Font Size Toggle
                Button(
                    onClick = onToggleFontSize,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = "A ${appState.fontSize.label}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Mute Toggle
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (appState.isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                        contentDescription = "Sound",
                        tint = Color(0xFF059669)
                    )
                }
            }
        }
    }
}
