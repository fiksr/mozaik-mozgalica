package rs.mozaikmozgalica.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Flag
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
import rs.mozaikmozgalica.app.util.SoundEffectsHelper

@Composable
fun AsocijacijeScreen(
    appState: AppState,
    isWorkoutActive: Boolean,
    onCompleteGame: (Int, Boolean) -> Unit,
    onBack: () -> Unit
) {
    val assocData = remember { QuizRepository.assocGrids.random() }
    val scrollState = rememberScrollState()

    var openClues by remember { mutableStateOf(setOf<String>()) }
    var solvedCols by remember { mutableStateOf(mapOf<String, String>()) }
    var earnedPoints by remember { mutableStateOf(0) }
    var isFinalSolved by remember { mutableStateOf(false) }
    var isGaveUp by remember { mutableStateOf(false) }

    var inputA by remember { mutableStateOf("") }
    var inputB by remember { mutableStateOf("") }
    var inputC by remember { mutableStateOf("") }
    var inputD by remember { mutableStateOf("") }
    var inputFinal by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
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

        // Top Grid (Columns A & B)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ColumnBox("A", assocData.colAClues, assocData.colASolution, openClues, solvedCols["A"], inputA, { inputA = it }, {
                if (inputA.trim().equals(assocData.colASolution, ignoreCase = true)) {
                    solvedCols = solvedCols + ("A" to assocData.colASolution)
                    earnedPoints += 10
                    SoundEffectsHelper.playCorrectDing()
                } else if (inputA.trim().isNotEmpty()) {
                    earnedPoints = maxOf(0, earnedPoints - 5)
                    SoundEffectsHelper.playWrongBuzz()
                }
            }, {
                solvedCols = solvedCols + ("A" to assocData.colASolution)
                earnedPoints += 5
                SoundEffectsHelper.playCorrectDing()
            }, { clueKey -> 
                SoundEffectsHelper.playClick()
                openClues = openClues + clueKey 
            }, appState, modifier = Modifier.weight(1f))

            ColumnBox("B", assocData.colBClues, assocData.colBSolution, openClues, solvedCols["B"], inputB, { inputB = it }, {
                if (inputB.trim().equals(assocData.colBSolution, ignoreCase = true)) {
                    solvedCols = solvedCols + ("B" to assocData.colBSolution)
                    earnedPoints += 10
                    SoundEffectsHelper.playCorrectDing()
                } else if (inputB.trim().isNotEmpty()) {
                    earnedPoints = maxOf(0, earnedPoints - 5)
                    SoundEffectsHelper.playWrongBuzz()
                }
            }, {
                solvedCols = solvedCols + ("B" to assocData.colBSolution)
                earnedPoints += 5
                SoundEffectsHelper.playCorrectDing()
            }, { clueKey -> 
                SoundEffectsHelper.playClick()
                openClues = openClues + clueKey 
            }, appState, modifier = Modifier.weight(1f))
        }

        // Final Solution Center Box
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = SerbianTransliteration.applyScript("Konačno Rešenje", appState.scriptType).uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFB45309)
                )

                if (isFinalSolved || isGaveUp) {
                    Text(
                        text = SerbianTransliteration.applyScript(assocData.finalSolution, appState.scriptType),
                        fontSize = (18 * appState.fontSize.scale).sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFD97706)
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = inputFinal,
                            onValueChange = { inputFinal = it },
                            placeholder = { Text("Konačno rešenje...", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                if (inputFinal.trim().equals(assocData.finalSolution, ignoreCase = true)) {
                                    isFinalSolved = true
                                    earnedPoints += 20
                                    AppStateManager.addPoints(earnedPoints)
                                    SoundEffectsHelper.playVictoryChime()
                                } else if (inputFinal.trim().isNotEmpty()) {
                                    earnedPoints = maxOf(0, earnedPoints - 5)
                                    SoundEffectsHelper.playWrongBuzz()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Potvrdi", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Bottom Grid (Columns C & D)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ColumnBox("C", assocData.colCClues, assocData.colCSolution, openClues, solvedCols["C"], inputC, { inputC = it }, {
                if (inputC.trim().equals(assocData.colCSolution, ignoreCase = true)) {
                    solvedCols = solvedCols + ("C" to assocData.colCSolution)
                    earnedPoints += 10
                    SoundEffectsHelper.playCorrectDing()
                } else if (inputC.trim().isNotEmpty()) {
                    earnedPoints = maxOf(0, earnedPoints - 5)
                    SoundEffectsHelper.playWrongBuzz()
                }
            }, {
                solvedCols = solvedCols + ("C" to assocData.colCSolution)
                earnedPoints += 5
                SoundEffectsHelper.playCorrectDing()
            }, { clueKey -> 
                SoundEffectsHelper.playClick()
                openClues = openClues + clueKey 
            }, appState, modifier = Modifier.weight(1f))

            ColumnBox("D", assocData.colDClues, assocData.colDSolution, openClues, solvedCols["D"], inputD, { inputD = it }, {
                if (inputD.trim().equals(assocData.colDSolution, ignoreCase = true)) {
                    solvedCols = solvedCols + ("D" to assocData.colDSolution)
                    earnedPoints += 10
                    SoundEffectsHelper.playCorrectDing()
                } else if (inputD.trim().isNotEmpty()) {
                    earnedPoints = maxOf(0, earnedPoints - 5)
                    SoundEffectsHelper.playWrongBuzz()
                }
            }, {
                solvedCols = solvedCols + ("D" to assocData.colDSolution)
                earnedPoints += 5
                SoundEffectsHelper.playCorrectDing()
            }, { clueKey -> 
                SoundEffectsHelper.playClick()
                openClues = openClues + clueKey 
            }, appState, modifier = Modifier.weight(1f))
        }

        // Give Up Button
        if (!isFinalSolved && !isGaveUp) {
            OutlinedButton(
                onClick = {
                    isGaveUp = true
                    solvedCols = mapOf(
                        "A" to assocData.colASolution,
                        "B" to assocData.colBSolution,
                        "C" to assocData.colCSolution,
                        "D" to assocData.colDSolution
                    )
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE11D48)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Flag, contentDescription = null, tint = Color(0xFFE11D48))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Odustajem - Prikaži Sve Odgovore", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        // Finish Game Button
        if (isFinalSolved || isGaveUp) {
            val nextBtnText = if (isWorkoutActive) "Sledeća Igra (Spojnice)" else "Vrati se na Početnu"
            Button(
                onClick = { onCompleteGame(earnedPoints, false) }, // completed naturally
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(SerbianTransliteration.applyScript(nextBtnText, appState.scriptType), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun ColumnBox(
    colName: String,
    clues: List<String>,
    solution: String,
    openClues: Set<String>,
    solvedSolution: String?,
    inputValue: String,
    onInputChange: (String) -> Unit,
    onSolve: () -> Unit,
    onHelp: () -> Unit,
    onOpenClue: (String) -> Unit,
    appState: AppState,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Kolona $colName",
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                color = Color(0xFFD97706)
            )

            if (solvedSolution != null) {
                Surface(
                    color = Color(0xFFF59E0B),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = SerbianTransliteration.applyScript(solvedSolution, appState.scriptType),
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            } else {
                clues.forEachIndexed { idx, clue ->
                    val clueKey = "$colName${idx + 1}"
                    val isOpen = openClues.contains(clueKey)
                    Surface(
                        color = if (isOpen) Color(0xFFFEF3C7) else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenClue(clueKey) }
                    ) {
                        Text(
                            text = if (isOpen) SerbianTransliteration.applyScript(clue, appState.scriptType) else "$clueKey: ???",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = inputValue,
                    onValueChange = onInputChange,
                    placeholder = { Text("Rešenje...", fontSize = 10.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = onSolve,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reši", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onHelp,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Pomoć", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
