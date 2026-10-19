package rs.mozaikmozgalica.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.mozaikmozgalica.app.data.*

@Composable
fun StatsScreen(
    appState: AppState
) {
    val scrollState = rememberScrollState()
    val userProfile by UserIdentityManager.profile.collectAsState()

    // Live leaderboard from Firebase (no dummy data)
    var liveCompetitors by remember { mutableStateOf<List<Competitor>>(emptyList()) }

    LaunchedEffect(appState.points, userProfile.userName) {
        UserIdentityManager.fetchLiveLeaderboard { fetched ->
            liveCompetitors = fetched
        }
    }

    val allCompetitors = remember(liveCompetitors, appState.points, userProfile.userName) {
        val userItem = Competitor(
            name = if (userProfile.userName.isNotEmpty()) userProfile.userName else "Vi (Vaš Skor)",
            points = appState.points,
            isUser = true
        )
        // Merge Firebase users with current user (avoid duplicate)
        val otherUsers = liveCompetitors.filter { !it.isUser }
        (otherUsers + userItem).sortedByDescending { it.points }
    }

    LaunchedEffect(appState.points) {
        UserIdentityManager.syncLeaderboardToFirebase()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = SerbianTransliteration.applyScript("Kognitivni Napredak: ${userProfile.userName}", appState.scriptType),
            fontSize = (16 * appState.fontSize.scale).sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Summary Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatRow("Ukupno Poena", "${appState.points}", Color(0xFF059669), appState)
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                StatRow("Rešenih Kvizova", "${appState.gamesPlayed}", MaterialTheme.colorScheme.onSurface, appState)
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                StatRow("Aktivni Niz (Niz Dana)", "${appState.streakDays} Dan", Color(0xFFD97706), appState)
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                StatRow("Status Licunce", if (userProfile.isLifetimeUnlocked) "Neograničeno (Paid)" else "Besplatno (2/dan)", Color(0xFF0284C7), appState)
            }
        }

        // Leaderboard Section
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFD97706))
                        Text(
                            text = SerbianTransliteration.applyScript("Rang Lista Takmičara", appState.scriptType),
                            fontSize = (13 * appState.fontSize.scale).sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val userPos = (allCompetitors.indexOfFirst { it.isUser }.takeIf { it >= 0 } ?: 0) + 1
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "#$userPos",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF92400E),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                allCompetitors.forEachIndexed { idx, player ->
                    val medalStr = when (idx) {
                        0 -> "🥇"
                        1 -> "🥈"
                        2 -> "🥉"
                        else -> "#${idx + 1}"
                    }

                    val bg = if (player.isUser) Color(0xFFD1FAE5) else MaterialTheme.colorScheme.surfaceVariant
                    val textCol = if (player.isUser) Color(0xFF065F46) else MaterialTheme.colorScheme.onSurfaceVariant

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = bg,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(medalStr, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                Icon(Icons.Default.Person, contentDescription = null, tint = textCol, modifier = Modifier.size(18.dp))
                                Text(
                                    text = SerbianTransliteration.applyScript(player.name, appState.scriptType),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = (12 * appState.fontSize.scale).sp,
                                    color = textCol
                                )
                            }
                            Text(
                                text = "${player.points} bod",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = (12 * appState.fontSize.scale).sp,
                                color = textCol
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String, valColor: Color, appState: AppState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(SerbianTransliteration.applyScript(label, appState.scriptType), fontSize = 12.sp, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Black, fontSize = 14.sp, color = valColor)
    }
}
