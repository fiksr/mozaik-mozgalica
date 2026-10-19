package rs.mozaikmozgalica.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.mozaikmozgalica.app.data.AppState
import rs.mozaikmozgalica.app.data.SerbianTransliteration
import rs.mozaikmozgalica.app.data.UserIdentityManager

@Composable
fun DashboardScreen(
    appState: AppState,
    onStartWorkout: (String) -> Unit,
    onOpenGame: (String) -> Unit,
    onOpenLeaderboard: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isMorningDone = UserIdentityManager.isMorningDoneToday()
    val isEveningDone = UserIdentityManager.isEveningDoneToday()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Daily Routine Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
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
                    Text(
                        text = SerbianTransliteration.applyScript("Dobrodošli!", appState.scriptType),
                        color = Color.White,
                        fontSize = (20 * appState.fontSize.scale).sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Surface(
                        color = Color(0xFF047857),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                            Text(
                                text = "${appState.streakDays} Dan",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Morning Workout Button
                    val mBg = if (isMorningDone) Color.Gray.copy(alpha = 0.5f) else Color(0xFFD97706)
                    val mLabel = if (isMorningDone) "✓ Jutarnji (Odrađeno)" else "☀️ Jutarnji Trening"

                    Button(
                        onClick = { onStartWorkout("morning") },
                        enabled = !isMorningDone,
                        colors = ButtonDefaults.buttonColors(containerColor = mBg),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = SerbianTransliteration.applyScript(mLabel, appState.scriptType),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color.White
                            )
                            Text("(Slagalica, Broj, KZZ)", fontSize = 9.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }

                    // Evening Workout Button
                    val eBg = if (isEveningDone) Color.Gray.copy(alpha = 0.5f) else Color(0xFF0284C7)
                    val eLabel = if (isEveningDone) "✓ Večernji (Odrađeno)" else "🌙 Večernji Trening"

                    Button(
                        onClick = { onStartWorkout("evening") },
                        enabled = !isEveningDone,
                        colors = ButtonDefaults.buttonColors(containerColor = eBg),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = SerbianTransliteration.applyScript(eLabel, appState.scriptType),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color.White
                            )
                            Text("(Srbija + Spojnice)", fontSize = 9.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                }
            }
        }

        // Stats Summary Badges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatBadgeCard("★", "${appState.points}", "Bodova", Color(0xFFD97706), appState, modifier = Modifier.weight(1f))
            StatBadgeCard("✓", "${appState.gamesPlayed}", "Kvizova", Color(0xFF059669), appState, modifier = Modifier.weight(1f))
            StatBadgeCard("🏆", appState.userRankTitle.split(" ").firstOrNull() ?: "Šampion", "Titula", Color(0xFF7C3AED), appState, modifier = Modifier.weight(1f))
        }

        // Leaderboard Banner Button
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenLeaderboard() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFFEF3C7), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                    }
                    Text(
                        text = SerbianTransliteration.applyScript("Vidi Rang Listu Ostalih Takmičara", appState.scriptType),
                        fontSize = (12 * appState.fontSize.scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
            }
        }

        // End of Dashboard Column Layout
    }
}

@Composable
fun StatBadgeCard(icon: String, value: String, label: String, color: Color, appState: AppState, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(icon, fontSize = 16.sp, color = color)
            Text(value, fontWeight = FontWeight.Black, fontSize = (15 * appState.fontSize.scale).sp, color = MaterialTheme.colorScheme.onSurface)
            Text(SerbianTransliteration.applyScript(label, appState.scriptType), fontSize = 9.sp, color = Color.Gray)
        }
    }
}

@Composable
fun QuizCardItem(symbol: String, title: String, subtitle: String, iconBg: Color, appState: AppState, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(iconBg, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(symbol, fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(
                        text = SerbianTransliteration.applyScript(title, appState.scriptType),
                        fontSize = (13 * appState.fontSize.scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = SerbianTransliteration.applyScript(subtitle, appState.scriptType),
                        fontSize = (10 * appState.fontSize.scale).sp,
                        color = Color.Gray
                    )
                }
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}
