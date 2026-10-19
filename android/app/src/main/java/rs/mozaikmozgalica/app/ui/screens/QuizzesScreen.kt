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
import androidx.compose.runtime.*
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
fun QuizzesScreen(
    appState: AppState,
    onOpenGame: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedCategory by remember { mutableStateOf("Sve") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = SerbianTransliteration.applyScript("Kolekcija Kvizova", appState.scriptType),
            fontSize = (18 * appState.fontSize.scale).sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Category Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Sve", "TV Kvizovi", "Srbija & Kultura").forEach { cat ->
                val isSelected = selectedCategory == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = cat },
                    label = {
                        Text(
                            text = SerbianTransliteration.applyScript(cat, appState.scriptType),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF059669),
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // Quiz Cards
        if (selectedCategory == "Sve" || selectedCategory == "TV Kvizovi") {
            DetailedQuizCard("TV Slagalica", "12 slova • Pretraga leksikona", "slagalica", Icons.Default.TextFields, Color(0xFF059669), appState, onOpenGame)
            DetailedQuizCard("Moj Broj", "Trocifreni cilj • Matematika", "mojbroj", Icons.Default.Calculate, Color(0xFF0284C7), appState, onOpenGame)
            DetailedQuizCard("Asocijacije", "4 kolone • Sistem Pomoć (5b)", "asocijacije", Icons.Default.GridView, Color(0xFFD97706), appState, onOpenGame)
            DetailedQuizCard("Spojnice", "2 runde • Poveži ~10 parova", "spojnice", Icons.Default.AccountTree, Color(0xFF7C3AED), appState, onOpenGame)
        }

        if (selectedCategory == "Sve" || selectedCategory == "Srbija & Kultura") {
            DetailedQuizCard("Ko Zna Zna", "10 pitanja • Opšta kultura", "koznazna", Icons.Default.Lightbulb, Color(0xFF4F46E5), appState, onOpenGame)
            DetailedQuizCard("Koliko Znaš Srbiju? 🇷🇸", "Manastiri, reke, planine i baština", "srbija", Icons.Default.Map, Color(0xFFB91C1C), appState, onOpenGame)
        }
    }
}

@Composable
fun DetailedQuizCard(
    title: String,
    subtitle: String,
    gameKey: String,
    icon: ImageVector,
    iconBg: Color,
    appState: AppState,
    onOpenGame: (String) -> Unit
) {
    val profileState by UserIdentityManager.profile.collectAsState()
    val playedCount = UserIdentityManager.getGamePlayedCount(gameKey)
    val isPremium = profileState.isLifetimeUnlocked
    val canPlay = isPremium || playedCount < 2

    val displaySubtitle = if (isPremium) {
        "$subtitle • Otključano"
    } else {
        "$subtitle • Pokrenuto danas: $playedCount / 2"
    }

    val cardBg = if (canPlay) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    val btnText = if (canPlay) "Započni" else "🔒 Otključaj"
    val btnColor = if (canPlay) iconBg else Color.Gray

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (canPlay) 3.dp else 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenGame(gameKey) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(if (canPlay) iconBg else Color.Gray, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White)
                }
                Column {
                    Text(
                        text = SerbianTransliteration.applyScript(title, appState.scriptType),
                        fontWeight = FontWeight.Bold,
                        fontSize = (14 * appState.fontSize.scale).sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = SerbianTransliteration.applyScript(displaySubtitle, appState.scriptType),
                        fontSize = (10 * appState.fontSize.scale).sp,
                        color = Color.Gray
                    )
                }
            }

            Button(
                onClick = { onOpenGame(gameKey) },
                colors = ButtonDefaults.buttonColors(containerColor = btnColor),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = SerbianTransliteration.applyScript(btnText, appState.scriptType),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
