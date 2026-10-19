package rs.mozaikmozgalica.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.mozaikmozgalica.app.data.*
import rs.mozaikmozgalica.app.util.SoundEffectsHelper

@Composable
fun TriviaGameScreen(
    titleName: String,
    questions: List<TriviaQuestion>,
    appState: AppState,
    isWorkoutActive: Boolean,
    onSpeak: (String) -> Unit,
    onCompleteGame: (Int, Boolean) -> Unit,
    onBack: () -> Unit
) {
    var currentIndex by remember { mutableStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var totalEarnedPoints by remember { mutableStateOf(0) }
    var isAnswered by remember { mutableStateOf(false) }

    val currentQ = questions.getOrNull(currentIndex) ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        SoundEffectsHelper.playClick()
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Nazad", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nazad", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                if (isWorkoutActive) {
                    Button(
                        onClick = {
                            SoundEffectsHelper.playClick()
                            onCompleteGame(0, true) // 0 points, isSkipped = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Preskoči", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Surface(
                color = Color(0xFFEEF2FF),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Pitanje ${currentIndex + 1} / ${questions.size}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF4F46E5),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            // Score Badge
            Surface(
                color = Color(0xFFFEF3C7),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                    Text(
                        text = "+$totalEarnedPoints bod",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF92400E)
                    )
                }
            }

            IconButton(
                onClick = { onSpeak("${currentQ.question}. Ponuđeni odgovori: ${currentQ.options.joinToString(", ")}") }
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = "Čitaj naglas", tint = Color(0xFFD97706))
            }
        }

        // Question Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = Color(0xFFE0E7FF),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = SerbianTransliteration.applyScript(currentQ.category, appState.scriptType).uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3730A3),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = SerbianTransliteration.applyScript(currentQ.question, appState.scriptType),
                    fontSize = (16 * appState.fontSize.scale).sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = (22 * appState.fontSize.scale).sp
                )

                // 4 Options
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    currentQ.options.forEachIndexed { idx, optText ->
                        val isCorrect = idx == currentQ.correctIndex
                        val isSelected = idx == selectedOptionIndex

                        val btnBg = when {
                            !isAnswered -> MaterialTheme.colorScheme.surfaceVariant
                            isCorrect -> Color(0xFFD1FAE5)
                            isSelected -> Color(0xFFFFE4E6)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }

                        val btnText = SerbianTransliteration.applyScript(optText, appState.scriptType)

                        Button(
                            onClick = {
                                if (!isAnswered) {
                                    selectedOptionIndex = idx
                                    isAnswered = true
                                    if (isCorrect) {
                                        totalEarnedPoints += 10
                                        SoundEffectsHelper.playCorrectDing()
                                    } else {
                                        SoundEffectsHelper.playWrongBuzz()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = btnBg),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = btnText,
                                    fontSize = (13 * appState.fontSize.scale).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (isAnswered && isCorrect) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669))
                                } else if (isAnswered && isSelected && !isCorrect) {
                                    Icon(Icons.Default.Cancel, contentDescription = null, tint = Color(0xFFE11D48))
                                }
                            }
                        }
                    }
                }

                // Educational Fun Fact Box
                if (isAnswered) {
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (selectedOptionIndex == currentQ.correctIndex) "TAČNO! (+10 poena)" else "ZANIMLJIVOST:",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = Color(0xFF92400E)
                            )
                            Text(
                                text = SerbianTransliteration.applyScript(currentQ.funFact, appState.scriptType),
                                fontSize = (11 * appState.fontSize.scale).sp,
                                color = Color(0xFF78350F)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Button(
                                onClick = {
                                    if (currentIndex + 1 < questions.size) {
                                        SoundEffectsHelper.playClick()
                                        currentIndex++
                                        selectedOptionIndex = null
                                        isAnswered = false
                                    } else {
                                        SoundEffectsHelper.playVictoryChime()
                                        AppStateManager.addPoints(totalEarnedPoints)
                                        onCompleteGame(totalEarnedPoints, false) // completed naturally
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val btnText = if (currentIndex + 1 < questions.size) {
                                    "Sledeće Pitanje"
                                } else {
                                    if (isWorkoutActive) {
                                        if (titleName.contains("Ko Zna Zna")) "Sledeća Igra (Koliko Znaš Srbiju?)" else "Završi Trening"
                                    } else {
                                        "Vrati se na Početnu"
                                    }
                                }
                                Text(
                                    text = SerbianTransliteration.applyScript(btnText, appState.scriptType),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
