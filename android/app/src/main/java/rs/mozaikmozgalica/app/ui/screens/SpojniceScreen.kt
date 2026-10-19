package rs.mozaikmozgalica.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rs.mozaikmozgalica.app.data.*
import rs.mozaikmozgalica.app.util.SoundEffectsHelper

@Composable
fun SpojniceScreen(
    appState: AppState,
    isWorkoutActive: Boolean,
    onCompleteGame: (Int, Boolean) -> Unit,
    onBack: () -> Unit
) {
    val activeRounds = remember { QuizRepository.spojnicePool.shuffled().take(2) }

    var roundIdx by remember { mutableStateOf(0) }
    var selectedLeftIdx by remember { mutableStateOf<Int?>(null) }
    var matchedPairs by remember { mutableStateOf(setOf<Int>()) }
    var rightMatchedIndices by remember { mutableStateOf(mapOf<Int, Int>()) }
    var earnedPoints by remember { mutableStateOf(0) }
    var wrongRightIdx by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    val currentRound = activeRounds.getOrNull(roundIdx) ?: return
    val rightList = remember(roundIdx) { currentRound.pairs.map { it.right }.shuffled() }

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

            Surface(
                color = Color(0xFFF3E8FF),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Runda ${roundIdx + 1} / ${activeRounds.size}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF7C3AED),
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = SerbianTransliteration.applyScript(currentRound.title, appState.scriptType),
                    fontSize = (15 * appState.fontSize.scale).sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = SerbianTransliteration.applyScript("Izaberite pojam sleva, pa odgovarajući desno", appState.scriptType),
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                // 2 Columns Grid (Left & Right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Left Column
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        currentRound.pairs.forEachIndexed { lIdx, pair ->
                            val isMatched = matchedPairs.contains(lIdx)
                            val isSelected = selectedLeftIdx == lIdx

                            val bg = when {
                                isMatched -> Color(0xFF059669)
                                isSelected -> Color(0xFFC084FC)
                                else -> Color(0xFFF3E8FF)
                            }
                            val textColor = if (isMatched) Color.White else Color(0xFF581C87)

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = bg,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 46.dp)
                                    .clickable(enabled = !isMatched) { selectedLeftIdx = lIdx }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = SerbianTransliteration.applyScript(pair.left, appState.scriptType),
                                        fontSize = (11 * appState.fontSize.scale).sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )
                                    if (isMatched) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Right Column
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rightList.forEachIndexed { rIdx, rightText ->
                            val matchedLeftIdx = rightMatchedIndices.entries.firstOrNull { it.value == rIdx }?.key
                            val isMatched = matchedLeftIdx != null

                            val bg = when {
                                isMatched -> Color(0xFF059669)
                                wrongRightIdx == rIdx -> Color(0xFFE11D48)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                            val textColor = if (isMatched) Color.White
                                else if (wrongRightIdx == rIdx) Color.White
                                else MaterialTheme.colorScheme.onSurfaceVariant

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = bg,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 46.dp)
                                    .clickable(enabled = !isMatched && selectedLeftIdx != null) {
                                        val lIdx = selectedLeftIdx ?: return@clickable
                                        val targetRight = currentRound.pairs[lIdx].right
                                        if (rightText == targetRight) {
                                            SoundEffectsHelper.playCorrectDing()
                                            matchedPairs = matchedPairs + lIdx
                                            rightMatchedIndices = rightMatchedIndices + (lIdx to rIdx)
                                            selectedLeftIdx = null
                                            earnedPoints += 15

                                            if (matchedPairs.size == currentRound.pairs.size) {
                                                if (roundIdx + 1 >= activeRounds.size) {
                                                    AppStateManager.addPoints(earnedPoints)
                                                    SoundEffectsHelper.playVictoryChime()
                                                }
                                            }
                                        } else {
                                            // Wrong answer — red flash + buzz + penalty
                                            SoundEffectsHelper.playWrongBuzz()
                                            earnedPoints = maxOf(0, earnedPoints - 5)
                                            wrongRightIdx = rIdx
                                            scope.launch {
                                                delay(600)
                                                wrongRightIdx = null
                                            }
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = SerbianTransliteration.applyScript(rightText, appState.scriptType),
                                        fontSize = (11 * appState.fontSize.scale).sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )
                                    if (isMatched) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Completion Box
                if (matchedPairs.size == currentRound.pairs.size) {
                    Surface(
                        color = Color(0xFFF3E8FF),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (roundIdx == 0) "Bravo! Završili ste 1. rundu Spojnica (+15 poena)!" else "ODLIČNO! Spojili ste sve parove! (+15 poena)",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = Color(0xFF6B21A8)
                            )
                            Button(
                                onClick = {
                                    if (roundIdx + 1 < activeRounds.size) {
                                        roundIdx++
                                        selectedLeftIdx = null
                                        matchedPairs = emptySet()
                                        rightMatchedIndices = emptyMap()
                                    } else {
                                        onCompleteGame(earnedPoints, false) // completed naturally
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val btnText = if (roundIdx + 1 < activeRounds.size) {
                                    "Pređi na Rundu 2"
                                } else {
                                    if (isWorkoutActive) "Sledeća Igra (Ko Zna Zna)" else "Vrati se na Početnu"
                                }
                                Text(
                                    text = SerbianTransliteration.applyScript(btnText, appState.scriptType),
                                    fontWeight = FontWeight.Bold,
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
