package rs.mozaikmozgalica.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
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
fun SettingsScreen(
    appState: AppState,
    onToggleScript: () -> Unit,
    onToggleFontSize: () -> Unit,
    onToggleSound: () -> Unit,
    onChangeName: () -> Unit,
    onResetDailyProgress: () -> Unit,
    onDeleteAccountConfirmed: () -> Unit
) {
    val scrollState = rememberScrollState()
    val userProfile by UserIdentityManager.profile.collectAsState()
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            icon = {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFE11D48), modifier = Modifier.size(36.dp))
            },
            title = {
                Text(
                    text = SerbianTransliteration.applyScript("Upozorenje: Brisanje Profila", appState.scriptType),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = (16 * appState.fontSize.scale).sp,
                    color = Color(0xFFE11D48)
                )
            },
            text = {
                Text(
                    text = SerbianTransliteration.applyScript(
                        "Da li ste sigurni da želite trajno da obrišete Vaš profil '${userProfile.userName}' i sve osvojene bodove (${appState.points} bod)? Ova radnja je konačna i ne može se poništiti.",
                        appState.scriptType
                    ),
                    fontSize = (12 * appState.fontSize.scale).sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        UserIdentityManager.deleteAccountAndResetData {
                            onDeleteAccountConfirmed()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Obriši Profil (Trajno)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmDialog = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(SerbianTransliteration.applyScript("Odustani", appState.scriptType), fontSize = 12.sp, color = Color.Gray)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = SerbianTransliteration.applyScript("Podešavanja & Profil", appState.scriptType),
            fontSize = (18 * appState.fontSize.scale).sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Section 1: Profil
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
                Text(
                    text = SerbianTransliteration.applyScript("KORISNIČKI PROFIL", appState.scriptType),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Gray
                )

                SettingRow(
                    title = "Ime / Nadimak",
                    subtitle = userProfile.userName,
                    actionLabel = "Promeni",
                    actionColor = Color(0xFFE0E7FF),
                    actionTextColor = Color(0xFF3730A3),
                    appState = appState,
                    onClick = onChangeName
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                SettingRow(
                    title = "Resetuj Dnevni Progres",
                    subtitle = "Obrišite današnje limite za testiranje",
                    actionLabel = "Resetuj",
                    actionColor = Color(0xFFFEF3C7),
                    actionTextColor = Color(0xFFD97706),
                    appState = appState,
                    onClick = onResetDailyProgress
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                // Delete Account Button Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = SerbianTransliteration.applyScript("Brisanje Profila i Bodova", appState.scriptType),
                            fontWeight = FontWeight.Bold,
                            fontSize = (13 * appState.fontSize.scale).sp,
                            color = Color(0xFFE11D48)
                        )
                        Text(
                            text = SerbianTransliteration.applyScript("Trajno briše profil i sve poene", appState.scriptType),
                            fontSize = (10 * appState.fontSize.scale).sp,
                            color = Color.Gray
                        )
                    }

                    Button(
                        onClick = { showDeleteConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE4E6), contentColor = Color(0xFFE11D48)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFE11D48), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Obriši", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Section 2: Prilagođavanje za Starije
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = SerbianTransliteration.applyScript("PRISTUPAČNOST (SENIOR UI)", appState.scriptType),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Gray
                )

                SettingRow(
                    title = "Pismo (Ćirilica / Latinica)",
                    subtitle = "Izaberite omiljeno pismo za čitanje",
                    actionLabel = if (appState.scriptType == ScriptType.CIRILICA) "Ћирилица" else "Latinica",
                    actionColor = Color(0xFFFEF3C7),
                    actionTextColor = Color(0xFF92400E),
                    appState = appState,
                    onClick = onToggleScript
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                SettingRow(
                    title = "Veličina Teksta",
                    subtitle = "Uvećajte slova za lakše čitanje",
                    actionLabel = appState.fontSize.label,
                    actionColor = MaterialTheme.colorScheme.surfaceVariant,
                    actionTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    appState = appState,
                    onClick = onToggleFontSize
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                SettingRow(
                    title = "Zvučni Efekti",
                    subtitle = "Zvuci za tačne i netačne odgovore",
                    actionLabel = if (appState.isMuted) "Isključeno" else "Uključeno",
                    actionColor = if (appState.isMuted) Color(0xFFFFE4E6) else Color(0xFFD1FAE5),
                    actionTextColor = if (appState.isMuted) Color(0xFFE11D48) else Color(0xFF059669),
                    appState = appState,
                    onClick = onToggleSound
                )
            }
        }
    }
}

@Composable
fun SettingRow(
    title: String,
    subtitle: String,
    actionLabel: String,
    actionColor: Color,
    actionTextColor: Color,
    appState: AppState,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = SerbianTransliteration.applyScript(title, appState.scriptType),
                fontWeight = FontWeight.Bold,
                fontSize = (13 * appState.fontSize.scale).sp
            )
            Text(
                text = SerbianTransliteration.applyScript(subtitle, appState.scriptType),
                fontSize = (10 * appState.fontSize.scale).sp,
                color = Color.Gray
            )
        }

        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = actionColor, contentColor = actionTextColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(actionLabel, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
    }
}
