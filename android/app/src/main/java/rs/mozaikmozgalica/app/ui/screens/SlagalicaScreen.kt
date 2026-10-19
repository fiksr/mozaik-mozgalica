package rs.mozaikmozgalica.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
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
import rs.mozaikmozgalica.app.util.SerbianDictionary
import rs.mozaikmozgalica.app.util.SoundEffectsHelper

@Composable
fun SlagalicaScreen(
    appState: AppState,
    isWorkoutActive: Boolean,
    onCompleteGame: (Int, Boolean) -> Unit,
    onBack: () -> Unit
) {
    // Always shuffle letters so they don't form the word in order
    val presetLetters = remember { QuizRepository.slagalicaPresets.random().shuffled() }
    var selectedTiles by remember { mutableStateOf(listOf<Pair<Int, String>>()) }
    var isSubmitted by remember { mutableStateOf(false) }
    var earnedPoints by remember { mutableStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Sastavite Najdužu Reč",
                    fontSize = (16 * appState.fontSize.scale).sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Answer Display Box
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 60.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (selectedTiles.isEmpty()) {
                            Text("Dodirnite slova ispod...", fontSize = 12.sp, color = Color.Gray)
                        } else {
                            selectedTiles.forEach { (_, char) ->
                                Surface(
                                    color = Color(0xFF059669),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.padding(2.dp)
                                ) {
                                    Text(
                                        text = SerbianTransliteration.applyScript(char, appState.scriptType),
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Error / Dictionary Message
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        fontSize = 11.sp,
                        color = Color(0xFFE11D48),
                        fontWeight = FontWeight.Bold
                    )
                }

                // 12 Letter Grid
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val rows = presetLetters.chunked(6)
                    rows.forEachIndexed { rIdx, rowList ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowList.forEachIndexed { cIdx, char ->
                                val globalIdx = rIdx * 6 + cIdx
                                val isUsed = selectedTiles.any { it.first == globalIdx }

                                Surface(
                                    color = if (isUsed) Color.Gray.copy(alpha = 0.3f) else Color(0xFF047857),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clickable(enabled = !isUsed && !isSubmitted) {
                                            SoundEffectsHelper.playClick()
                                            errorMessage = null
                                            selectedTiles = selectedTiles + (globalIdx to char)
                                        }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = SerbianTransliteration.applyScript(char, appState.scriptType),
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 18.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Action Buttons (Delete last letter, reset, confirm)
                if (!isSubmitted) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Delete last letter button
                            OutlinedButton(
                                onClick = {
                                    if (selectedTiles.isNotEmpty()) {
                                        SoundEffectsHelper.playClick()
                                        errorMessage = null
                                        selectedTiles = selectedTiles.dropLast(1)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Backspace, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Obriši Slovo", fontSize = 11.sp)
                            }

                            // Reset all letters button
                            OutlinedButton(
                                onClick = {
                                    SoundEffectsHelper.playClick()
                                    errorMessage = null
                                    selectedTiles = emptyList()
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Obriši Sve", fontSize = 11.sp)
                            }
                        }

                        Button(
                            onClick = {
                                val userWord = selectedTiles.joinToString("") { it.second }
                                if (userWord.length < 3) {
                                    SoundEffectsHelper.playWrongBuzz()
                                    errorMessage = "Reč mora imati najmanje 3 slova!"
                                } else if (!SerbianDictionary.isValidWord(userWord)) {
                                    SoundEffectsHelper.playWrongBuzz()
                                    errorMessage = "Reč '$userWord' nije pronađena u srpskom rečniku!"
                                } else {
                                    SoundEffectsHelper.playVictoryChime()
                                    earnedPoints = userWord.length * 3
                                    isSubmitted = true
                                    AppStateManager.addPoints(earnedPoints)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Potvrdi Reč", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                } else {
                    Surface(
                        color = Color(0xFFD1FAE5),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Sjajno! Reč '${selectedTiles.joinToString("") { it.second }}' je verifikovana (+$earnedPoints poena)!",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = Color(0xFF065F46)
                            )
                            val nextBtnText = if (isWorkoutActive) "Sledeća Igra (Moj Broj)" else "Vrati se na Početnu"
                            Button(
                                onClick = {
                                    SoundEffectsHelper.playClick()
                                    onCompleteGame(earnedPoints, false) // completed naturally
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
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
