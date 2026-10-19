package rs.mozaikmozgalica.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.mozaikmozgalica.app.data.AppState
import rs.mozaikmozgalica.app.data.SerbianTransliteration

@Composable
fun NicknameDialog(
    appState: AppState,
    onSaveName: (String) -> Unit
) {
    var nameInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { /* Require input */ },
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = SerbianTransliteration.applyScript("Dobrodošli u Mozaik Mozgalicu! 🧠", appState.scriptType),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = (18 * appState.fontSize.scale).sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = SerbianTransliteration.applyScript("Kako želite da Vas zovemo u kvizu?", appState.scriptType),
                    fontSize = (12 * appState.fontSize.scale).sp,
                    color = Color.Gray
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    placeholder = {
                        Text(
                            text = SerbianTransliteration.applyScript("npr. Baka Slavica ili Deda Jovan", appState.scriptType),
                            fontSize = 12.sp
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val name = if (nameInput.trim().isNotEmpty()) nameInput.trim() else "Penzioner Kvizolog"
                    onSaveName(name)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = SerbianTransliteration.applyScript("Sačuvaj i Započni Trening", appState.scriptType),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
    )
}
