package rs.mozaikmozgalica.app

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rs.mozaikmozgalica.app.data.*
import rs.mozaikmozgalica.app.theme.MozaikMozgalicaTheme
import rs.mozaikmozgalica.app.ui.components.NicknameDialog
import rs.mozaikmozgalica.app.ui.components.SeniorBottomNavBar
import rs.mozaikmozgalica.app.ui.components.SeniorTopToolbar
import rs.mozaikmozgalica.app.ui.components.UnlockModal
import rs.mozaikmozgalica.app.ui.screens.*
import rs.mozaikmozgalica.app.util.SerbianDictionary
import rs.mozaikmozgalica.app.util.TextToSpeechHelper

class MainActivity : ComponentActivity() {
    private var ttsHelper: TextToSpeechHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ttsHelper = TextToSpeechHelper(this)

        // Load 94,400+ official Serbian words dictionary asynchronously in background thread
        lifecycleScope.launch(Dispatchers.IO) {
            SerbianDictionary.loadDictionary(this@MainActivity)
        }

        // Load Asocijacije dynamically from asocijacije.json asset
        QuizRepository.loadAssociationsFromAssets(this)

        // Sync remote questions & associations from Firebase in background thread
        lifecycleScope.launch(Dispatchers.IO) {
            QuizRepository.syncContentFromFirebase(this@MainActivity)
        }

        val prefs = getSharedPreferences("mozaik_prefs", Context.MODE_PRIVATE)
        val savedName = prefs.getString("user_name", null)
        val savedId = prefs.getString("user_id", null)
        val savedPoints = prefs.getInt("user_points", 0)
        val savedGames = prefs.getInt("user_games", 0)
        val savedStreak = prefs.getInt("user_streak", 1)
        val playedSlagalica = prefs.getInt("played_slagalica", 0)
        val playedMojbroj = prefs.getInt("played_mojbroj", 0)
        val playedAsocijacije = prefs.getInt("played_asocijacije", 0)
        val playedSpojnice = prefs.getInt("played_spojnice", 0)
        val playedKoznazna = prefs.getInt("played_koznazna", 0)
        val playedSrbija = prefs.getInt("played_srbija", 0)
        val morningDate = prefs.getString("morning_done_date", "") ?: ""
        val eveningDate = prefs.getString("evening_done_date", "") ?: ""
        val isUnlocked = prefs.getBoolean("is_unlocked", false)

        // Restore points, games and streak on startup
        AppStateManager.restoreState(savedPoints, savedGames, savedStreak)

        UserIdentityManager.initUser(savedName, savedId)
        UserIdentityManager.restoreFullProfile(
            playedSlagalica, playedMojbroj, playedAsocijacije, playedSpojnice, playedKoznazna, playedSrbija,
            morningDate, eveningDate, isUnlocked
        )

        // Check remote Pro status from Firebase (admin can unlock remotely from Firebase Console)
        UserIdentityManager.checkRemoteProStatus { isPro ->
            if (isPro) {
                saveProfileToPrefs(prefs)
            }
        }

        // Listen for points updates to save to SharedPreferences
        AppStateManager.onStateChangedListener = { points, games, streak ->
            saveProfileToPrefs(prefs)
        }

        enableEdgeToEdge()
        setContent {
            MozaikMozgalicaTheme {
                MainAppScreen(
                    ttsHelper = ttsHelper,
                    savedName = savedName,
                    onSaveNamePref = {
                        saveProfileToPrefs(prefs)
                    },
                    onDeleteAccountPref = {
                        prefs.edit().clear().apply()
                    },
                    prefs = prefs
                )
            }
        }
    }

    override fun onDestroy() {
        ttsHelper?.shutdown()
        super.onDestroy()
    }
}

fun saveProfileToPrefs(prefs: SharedPreferences) {
    val profile = UserIdentityManager.profile.value
    prefs.edit().apply {
        putString("user_name", profile.userName)
        putString("user_id", profile.userId)
        putInt("user_points", AppStateManager.state.value.points)
        putInt("user_games", AppStateManager.state.value.gamesPlayed)
        putInt("user_streak", AppStateManager.state.value.streakDays)
        putInt("played_slagalica", profile.slagalicaPlayedToday)
        putInt("played_mojbroj", profile.mojbrojPlayedToday)
        putInt("played_asocijacije", profile.asocijacijePlayedToday)
        putInt("played_spojnice", profile.spojnicePlayedToday)
        putInt("played_koznazna", profile.koznaznaPlayedToday)
        putInt("played_srbija", profile.srbijaPlayedToday)
        putString("morning_done_date", profile.morningDoneDate)
        putString("evening_done_date", profile.eveningDoneDate)
        putBoolean("is_unlocked", profile.isLifetimeUnlocked)
        apply()
    }
}

@Composable
fun MainAppScreen(
    ttsHelper: TextToSpeechHelper?,
    savedName: String?,
    onSaveNamePref: (String) -> Unit,
    onDeleteAccountPref: () -> Unit,
    prefs: SharedPreferences
) {
    val appState by AppStateManager.state.collectAsStateWithLifecycle()
    val userProfile by UserIdentityManager.profile.collectAsStateWithLifecycle()

    var currentScreen by remember { mutableStateOf("dashboard") }
    var currentTab by remember { mutableStateOf("dashboard") }

    var activeWorkoutType by remember { mutableStateOf<String?>(null) }

    var showNicknameDialog by remember { mutableStateOf(savedName.isNullOrEmpty()) }
    var showUnlockModal by remember { mutableStateOf(false) }

    if (showNicknameDialog) {
        NicknameDialog(
            appState = appState,
            onSaveName = { name ->
                UserIdentityManager.setUserName(name) {
                    onSaveNamePref(name)
                }
                showNicknameDialog = false
            }
        )
    }

    if (showUnlockModal) {
        UnlockModal(
            appState = appState,
            onUnlockClicked = {
                UserIdentityManager.unlockLifetimeAccess()
                saveProfileToPrefs(prefs)
                showUnlockModal = false
            },
            onDismiss = { showUnlockModal = false }
        )
    }

    // Dynamic workout sequence progression router
    fun handleWorkoutNextStep(currentGameKey: String, workoutType: String) {
        val currentIndex = UserIdentityManager.workoutSequence.indexOf(currentGameKey)
        val nextIndex = currentIndex + 1
        if (nextIndex < UserIdentityManager.workoutSequence.size) {
            currentScreen = UserIdentityManager.workoutSequence[nextIndex]
        } else {
            // Workout complete!
            UserIdentityManager.recordWorkoutCompleted(workoutType)
            saveProfileToPrefs(prefs)
            activeWorkoutType = null
            currentScreen = "dashboard"
        }
    }

    Scaffold(
        topBar = {
            SeniorTopToolbar(
                appState = appState,
                onToggleScript = { AppStateManager.toggleScript() },
                onToggleFontSize = { AppStateManager.toggleFontSize() },
                onToggleSound = { AppStateManager.toggleMute() },
                onLogoClick = {
                    currentScreen = "dashboard"
                    currentTab = "dashboard"
                }
            )
        },
        bottomBar = {
            SeniorBottomNavBar(
                currentTab = currentTab,
                appState = appState,
                onTabSelected = { tab ->
                    currentTab = tab
                    currentScreen = tab
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                "dashboard" -> {
                    DashboardScreen(
                        appState = appState,
                        onStartWorkout = { workoutType ->
                            if (UserIdentityManager.canPlayWorkout(workoutType)) {
                                activeWorkoutType = workoutType
                                val nextGame = UserIdentityManager.getNextWorkoutScreen(workoutType)
                                if (nextGame != null) {
                                    currentScreen = nextGame
                                } else {
                                    showUnlockModal = true
                                }
                            } else {
                                showUnlockModal = true
                            }
                        },
                        onOpenGame = { gameKey ->
                            if (UserIdentityManager.canPlayGame(gameKey)) {
                                activeWorkoutType = null
                                currentScreen = gameKey
                            } else {
                                showUnlockModal = true
                            }
                        },
                        onOpenLeaderboard = {
                            currentTab = "stats"
                            currentScreen = "stats"
                        }
                    )
                }
                "games" -> {
                    QuizzesScreen(
                        appState = appState,
                        onOpenGame = { gameKey ->
                            if (UserIdentityManager.canPlayGame(gameKey)) {
                                activeWorkoutType = null
                                currentScreen = gameKey
                            } else {
                                showUnlockModal = true
                            }
                        }
                    )
                }
                "slagalica" -> {
                    SlagalicaScreen(
                        appState = appState,
                        isWorkoutActive = activeWorkoutType != null,
                        onCompleteGame = { _, isSkipped ->
                            if (!isSkipped) {
                                UserIdentityManager.recordGamePlayed("slagalica") {
                                    saveProfileToPrefs(prefs)
                                }
                            }
                            if (activeWorkoutType != null) {
                                handleWorkoutNextStep("slagalica", activeWorkoutType!!)
                            } else {
                                currentScreen = "dashboard"
                            }
                        },
                        onBack = { currentScreen = "dashboard" }
                    )
                }
                "mojbroj" -> {
                    MojBrojScreen(
                        appState = appState,
                        isWorkoutActive = activeWorkoutType != null,
                        onCompleteGame = { _, isSkipped ->
                            if (!isSkipped) {
                                UserIdentityManager.recordGamePlayed("mojbroj") {
                                    saveProfileToPrefs(prefs)
                                }
                            }
                            if (activeWorkoutType != null) {
                                handleWorkoutNextStep("mojbroj", activeWorkoutType!!)
                            } else {
                                currentScreen = "dashboard"
                            }
                        },
                        onBack = { currentScreen = "dashboard" }
                    )
                }
                "asocijacije" -> {
                    AsocijacijeScreen(
                        appState = appState,
                        isWorkoutActive = activeWorkoutType != null,
                        onCompleteGame = { _, isSkipped ->
                            if (!isSkipped) {
                                UserIdentityManager.recordGamePlayed("asocijacije") {
                                    saveProfileToPrefs(prefs)
                                }
                            }
                            if (activeWorkoutType != null) {
                                handleWorkoutNextStep("asocijacije", activeWorkoutType!!)
                            } else {
                                currentScreen = "dashboard"
                            }
                        },
                        onBack = { currentScreen = "dashboard" }
                    )
                }
                "spojnice" -> {
                    SpojniceScreen(
                        appState = appState,
                        isWorkoutActive = activeWorkoutType != null,
                        onCompleteGame = { _, isSkipped ->
                            if (!isSkipped) {
                                UserIdentityManager.recordGamePlayed("spojnice") {
                                    saveProfileToPrefs(prefs)
                                }
                            }
                            if (activeWorkoutType != null) {
                                handleWorkoutNextStep("spojnice", activeWorkoutType!!)
                            } else {
                                currentScreen = "dashboard"
                            }
                        },
                        onBack = { currentScreen = "dashboard" }
                    )
                }
                "koznazna" -> {
                    val gameQuestions = remember(currentScreen) {
                        val pool = QuizRepository.koZnaZnaQuestions
                        if (pool.size > 10) pool.shuffled().take(10) else pool
                    }
                    TriviaGameScreen(
                        titleName = "Ko Zna Zna",
                        questions = gameQuestions,
                        appState = appState,
                        isWorkoutActive = activeWorkoutType != null,
                        onSpeak = { text -> ttsHelper?.speak(text, appState.scriptType) },
                        onCompleteGame = { _, isSkipped ->
                            if (!isSkipped) {
                                UserIdentityManager.recordGamePlayed("koznazna") {
                                    saveProfileToPrefs(prefs)
                                }
                            }
                            if (activeWorkoutType != null) {
                                handleWorkoutNextStep("koznazna", activeWorkoutType!!)
                            } else {
                                currentScreen = "dashboard"
                            }
                        },
                        onBack = { currentScreen = "dashboard" }
                    )
                }
                "srbija" -> {
                    val gameQuestions = remember(currentScreen) {
                        val pool = QuizRepository.kolikoZnasSrbijuQuestions
                        if (pool.size > 10) pool.shuffled().take(10) else pool
                    }
                    TriviaGameScreen(
                        titleName = "Koliko Znaš Srbiju? 🇷🇸",
                        questions = gameQuestions,
                        appState = appState,
                        isWorkoutActive = activeWorkoutType != null,
                        onSpeak = { text -> ttsHelper?.speak(text, appState.scriptType) },
                        onCompleteGame = { _, isSkipped ->
                            if (!isSkipped) {
                                UserIdentityManager.recordGamePlayed("srbija") {
                                    saveProfileToPrefs(prefs)
                                }
                            }
                            if (activeWorkoutType != null) {
                                handleWorkoutNextStep("srbija", activeWorkoutType!!)
                            } else {
                                currentScreen = "dashboard"
                            }
                        },
                        onBack = { currentScreen = "dashboard" }
                    )
                }
                "stats" -> {
                    StatsScreen(appState = appState)
                }
                "settings" -> {
                    SettingsScreen(
                        appState = appState,
                        onToggleScript = { AppStateManager.toggleScript() },
                        onToggleFontSize = { AppStateManager.toggleFontSize() },
                        onToggleSound = { AppStateManager.toggleMute() },
                        onChangeName = { showNicknameDialog = true },
                        onResetDailyProgress = {
                            UserIdentityManager.resetDailyProgress {
                                saveProfileToPrefs(prefs)
                            }
                        },
                        onDeleteAccountConfirmed = {
                            onDeleteAccountPref()
                            showNicknameDialog = true
                            currentScreen = "dashboard"
                            currentTab = "dashboard"
                        }
                    )
                }
            }
        }
    }
}
