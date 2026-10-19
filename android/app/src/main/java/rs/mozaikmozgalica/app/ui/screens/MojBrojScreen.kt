package rs.mozaikmozgalica.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.mozaikmozgalica.app.data.*
import rs.mozaikmozgalica.app.util.SerbianMathEvaluator
import rs.mozaikmozgalica.app.util.SoundEffectsHelper
import kotlin.math.abs

@Composable
fun MojBrojScreen(
    appState: AppState,
    isWorkoutActive: Boolean,
    onCompleteGame: (Int, Boolean) -> Unit,
    onBack: () -> Unit
) {
    val targetNumber = remember { (100..999).random() }
    val numberTiles = remember {
        listOf(
            (1..9).random(), (1..9).random(), (1..9).random(), (1..9).random(),
            listOf(10, 15, 20).random(), listOf(25, 50, 75, 100).random()
        )
    }

    var usedTileIndices by remember { mutableStateOf(setOf<Int>()) }
    var expressionTokens by remember { mutableStateOf(listOf<String>()) }
    var isSubmitted by remember { mutableStateOf(false) }
    var calculatedResult by remember { mutableStateOf<Int?>(null) }
    var earnedPoints by remember { mutableStateOf(0) }
    var resultMessage by remember { mutableStateOf("") }

    val currentExprStr = expressionTokens.joinToString(" ")

    // 100% Mathematically Exact Evaluation via Shunting-Yard
    val liveResultStr = remember(currentExprStr) {
        SerbianMathEvaluator.evaluate(currentExprStr)?.toString()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
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
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
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

            // Current Points Badge
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
                        text = "+$earnedPoints bod",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF92400E)
                    )
                }
            }
        }

        // Card Container
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Target Box
                Surface(
                    color = Color(0xFFF0F9FF),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("TRAŽENI BROJ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                        Text("$targetNumber", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color(0xFF0C4A6E))
                    }
                }

                // Calculation Expression Bar with Live Result "= X"
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (currentExprStr.isEmpty()) "Sastavite račun..." else currentExprStr,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        if (liveResultStr != null) {
                            Surface(
                                color = Color(0xFF0284C7),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "= $liveResultStr",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // 6 Number Tiles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    numberTiles.forEachIndexed { idx, valNum ->
                        val isUsed = usedTileIndices.contains(idx)
                        Surface(
                            color = if (isUsed) Color.Gray.copy(alpha = 0.3f) else Color(0xFF0369A1),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clickable(enabled = !isUsed && !isSubmitted) {
                                    SoundEffectsHelper.playClick()
                                    usedTileIndices = usedTileIndices + idx
                                    expressionTokens = expressionTokens + "$valNum"
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("$valNum", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            }
                        }
                    }
                }

                // Operator Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("+", "-", "*", "/", "(", ")").forEach { op ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clickable(enabled = !isSubmitted) {
                                    SoundEffectsHelper.playClick()
                                    expressionTokens = expressionTokens + op
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(op, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            }
                        }
                    }
                }

                // Actions
                if (!isSubmitted) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Backspace / Undo last token button
                        OutlinedButton(
                            onClick = {
                                if (expressionTokens.isNotEmpty()) {
                                    SoundEffectsHelper.playClick()
                                    val lastToken = expressionTokens.last()
                                    expressionTokens = expressionTokens.dropLast(1)
                                    val numValue = lastToken.toIntOrNull()
                                    if (numValue != null) {
                                        val tileToFree = usedTileIndices.firstOrNull { idx ->
                                            numberTiles[idx] == numValue
                                        }
                                        if (tileToFree != null) {
                                            usedTileIndices = usedTileIndices - tileToFree
                                        }
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text("Obriši", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Reset button
                        OutlinedButton(
                            onClick = {
                                SoundEffectsHelper.playClick()
                                usedTileIndices = emptySet()
                                expressionTokens = emptyList()
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text("Resetuj", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            enabled = liveResultStr != null,
                            onClick = {
                                val userVal = liveResultStr?.toIntOrNull() ?: 0
                                calculatedResult = userVal
                                val diff = abs(userVal - targetNumber)

                                val (pts, msg) = when {
                                    userVal == targetNumber -> {
                                        SoundEffectsHelper.playVictoryChime()
                                        30 to "SAVRŠENO! Dobili ste tačan broj $targetNumber! (+30 poena)"
                                    }
                                    diff <= 5 -> {
                                        SoundEffectsHelper.playCorrectDing()
                                        15 to "VEOMA BLIZU! Dobili ste $userVal (razlika samo $diff) (+15 poena)"
                                    }
                                    diff <= 20 -> {
                                        SoundEffectsHelper.playCorrectDing()
                                        10 to "DOBAR POKUŠAJ! Dobili ste $userVal (traženo $targetNumber) (+10 poena)"
                                    }
                                    else -> {
                                        SoundEffectsHelper.playWrongBuzz()
                                        5 to "TRENING SE RAČUNA! Dobili ste $userVal (traženo $targetNumber) (+5 poena)"
                                    }
                                }

                                earnedPoints = pts
                                resultMessage = msg
                                isSubmitted = true
                                AppStateManager.addPoints(earnedPoints)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(2f)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Potvrdi Rešenje", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                } else {
                    Surface(
                        color = Color(0xFFE0F2FE),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = resultMessage,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = Color(0xFF0369A1)
                            )
                            val nextBtnText = if (isWorkoutActive) "Sledeća Igra (Asocijacije)" else "Vrati se na Početnu"
                            Button(
                                onClick = {
                                    SoundEffectsHelper.playClick()
                                    onCompleteGame(earnedPoints, false) // completed naturally
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(SerbianTransliteration.applyScript(nextBtnText, appState.scriptType), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
