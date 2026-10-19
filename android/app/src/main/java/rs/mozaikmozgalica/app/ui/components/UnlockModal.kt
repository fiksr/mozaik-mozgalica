package rs.mozaikmozgalica.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.mozaikmozgalica.app.data.AppState
import rs.mozaikmozgalica.app.data.SerbianTransliteration

@Composable
fun UnlockModal(
    appState: AppState,
    onUnlockClicked: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        icon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFFEF3C7), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(24.dp))
            }
        },
        title = {
            Text(
                text = SerbianTransliteration.applyScript("Dnevni Limit Postignut! 🌟", appState.scriptType),
                fontWeight = FontWeight.ExtraBold,
                fontSize = (16 * appState.fontSize.scale).sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = SerbianTransliteration.applyScript(
                        "Iskoristili ste Vaša 2 besplatna treninga za danas! Uspešno ste vežbali um.",
                        appState.scriptType
                    ),
                    fontSize = (12 * appState.fontSize.scale).sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = SerbianTransliteration.applyScript(
                        "Vidimo se sutra u novom treningu ili otključajte neograničeno igranje za celu porodicu jednim klikom.",
                        appState.scriptType
                    ),
                    fontSize = (11 * appState.fontSize.scale).sp,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onUnlockClicked,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Otključaj Neograničeno (299 RSD)",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = Color.Black
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = SerbianTransliteration.applyScript("Razumem, vidimo se sutra", appState.scriptType),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    )
}
