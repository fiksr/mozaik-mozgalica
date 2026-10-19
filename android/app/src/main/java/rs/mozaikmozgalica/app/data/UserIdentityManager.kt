package rs.mozaikmozgalica.app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class UserProfile(
    val userId: String = "",
    val userName: String = "Penzioner Kvizolog",
    val isLifetimeUnlocked: Boolean = false,
    val workoutsToday: Int = 0,
    val gamesPlayedToday: Int = 0,
    val morningDoneDate: String = "",
    val eveningDoneDate: String = "",
    val lastWorkoutDate: String = "",
    val totalPoints: Int = 0,
    val streakDays: Int = 1,
    val rankTitle: String = "Radoznali Um",
    val slagalicaPlayedToday: Int = 0,
    val mojbrojPlayedToday: Int = 0,
    val asocijacijePlayedToday: Int = 0,
    val spojnicePlayedToday: Int = 0,
    val koznaznaPlayedToday: Int = 0,
    val srbijaPlayedToday: Int = 0
)

object UserIdentityManager {
    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    private val firestore by lazy { FirebaseFirestore.getInstance() }

    // Official order of all 6 games inside a full workout
    val workoutSequence = listOf("slagalica", "mojbroj", "asocijacije", "spojnice", "koznazna", "srbija")

    fun initUser(savedName: String?, savedId: String?) {
        val uid = savedId ?: UUID.randomUUID().toString()
        val name = if (!savedName.isNullOrEmpty()) savedName else "Igrač ${uid.take(4)}"
        val todayStr = getTodayDateString()

        _profile.value = _profile.value.copy(
            userId = uid,
            userName = name,
            lastWorkoutDate = todayStr
        )
    }

    fun restoreFullProfile(
        slagalicaPlayed: Int,
        mojbrojPlayed: Int,
        asocijacijePlayed: Int,
        spojnicePlayed: Int,
        koznaznaPlayed: Int,
        srbijaPlayed: Int,
        morningDate: String,
        eveningDate: String,
        isUnlocked: Boolean
    ) {
        _profile.value = _profile.value.copy(
            slagalicaPlayedToday = slagalicaPlayed,
            mojbrojPlayedToday = mojbrojPlayed,
            asocijacijePlayedToday = asocijacijePlayed,
            spojnicePlayedToday = spojnicePlayed,
            koznaznaPlayedToday = koznaznaPlayed,
            srbijaPlayedToday = srbijaPlayed,
            morningDoneDate = morningDate,
            eveningDoneDate = eveningDate,
            isLifetimeUnlocked = isUnlocked
        )
    }

    fun setUserName(name: String, onDone: () -> Unit = {}) {
        val cleanName = if (name.trim().isNotEmpty()) name.trim() else "Kvizolog"
        try {
            firestore.collection("leaderboard")
                .whereEqualTo("name", cleanName)
                .limit(1)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot != null && !querySnapshot.isEmpty) {
                        val doc = querySnapshot.documents[0]
                        val uid = doc.getString("userId") ?: UUID.randomUUID().toString()
                        val points = doc.getLong("points")?.toInt() ?: 0
                        val streak = doc.getLong("streakDays")?.toInt() ?: 1
                        val isPro = doc.getBoolean("isPro") ?: false

                        _profile.value = _profile.value.copy(
                            userId = uid,
                            userName = cleanName,
                            isLifetimeUnlocked = isPro
                        )
                        AppStateManager.restoreState(points, AppStateManager.state.value.gamesPlayed, streak)
                    } else {
                        // New user: create a brand new UUID and start fresh
                        val newUid = UUID.randomUUID().toString()
                        _profile.value = _profile.value.copy(
                            userId = newUid,
                            userName = cleanName,
                            isLifetimeUnlocked = false
                        )
                        AppStateManager.restoreState(0, 0, 1)
                    }
                    syncLeaderboardToFirebase()
                    onDone()
                }
                .addOnFailureListener {
                    // Fallback: If offline and not existing locally, keep working
                    val newUid = UUID.randomUUID().toString()
                    _profile.value = _profile.value.copy(
                        userId = newUid,
                        userName = cleanName
                    )
                    AppStateManager.restoreState(0, 0, 1)
                    syncLeaderboardToFirebase()
                    onDone()
                }
        } catch (e: Exception) {
            val newUid = UUID.randomUUID().toString()
            _profile.value = _profile.value.copy(
                userId = newUid,
                userName = cleanName
            )
            AppStateManager.restoreState(0, 0, 1)
            syncLeaderboardToFirebase()
            onDone()
        }
    }

    fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun isMorningDoneToday(): Boolean {
        if (_profile.value.isLifetimeUnlocked) return false
        val today = getTodayDateString()
        if (_profile.value.lastWorkoutDate != today) return false
        // Morning workout is done when all 6 games are played at least once
        return workoutSequence.all { getGamePlayedCount(it) >= 1 }
    }

    fun isEveningDoneToday(): Boolean {
        if (_profile.value.isLifetimeUnlocked) return false
        val today = getTodayDateString()
        if (_profile.value.lastWorkoutDate != today) return false
        // Evening workout is done when all 6 games are played at least twice
        return workoutSequence.all { getGamePlayedCount(it) >= 2 }
    }

    fun canPlayWorkout(type: String): Boolean {
        if (_profile.value.isLifetimeUnlocked) return true
        return if (type == "morning") {
            !isMorningDoneToday()
        } else {
            !isEveningDoneToday()
        }
    }

    fun getNextWorkoutScreen(workoutType: String): String? {
        val targetLimit = if (workoutType == "morning") 1 else 2
        for (gameKey in workoutSequence) {
            if (getGamePlayedCount(gameKey) < targetLimit) {
                return gameKey
            }
        }
        return null // All games played to target count!
    }

    fun getGamePlayedCount(gameKey: String): Int {
        val today = getTodayDateString()
        if (_profile.value.lastWorkoutDate != today) return 0
        return when (gameKey) {
            "slagalica" -> _profile.value.slagalicaPlayedToday
            "mojbroj" -> _profile.value.mojbrojPlayedToday
            "asocijacije" -> _profile.value.asocijacijePlayedToday
            "spojnice" -> _profile.value.spojnicePlayedToday
            "koznazna" -> _profile.value.koznaznaPlayedToday
            "srbija" -> _profile.value.srbijaPlayedToday
            else -> 0
        }
    }

    fun canPlayGame(gameKey: String): Boolean {
        if (_profile.value.isLifetimeUnlocked) return true
        
        // Reset counters locally if day changed
        val today = getTodayDateString()
        if (_profile.value.lastWorkoutDate != today) {
            _profile.value = _profile.value.copy(
                lastWorkoutDate = today,
                slagalicaPlayedToday = 0,
                mojbrojPlayedToday = 0,
                asocijacijePlayedToday = 0,
                spojnicePlayedToday = 0,
                koznaznaPlayedToday = 0,
                srbijaPlayedToday = 0,
                workoutsToday = 0,
                gamesPlayedToday = 0
            )
            return true
        }

        return getGamePlayedCount(gameKey) < 2
    }

    fun resetDailyProgress(onStateChanged: () -> Unit = {}) {
        val today = getTodayDateString()
        _profile.value = _profile.value.copy(
            lastWorkoutDate = today,
            slagalicaPlayedToday = 0,
            mojbrojPlayedToday = 0,
            asocijacijePlayedToday = 0,
            spojnicePlayedToday = 0,
            koznaznaPlayedToday = 0,
            srbijaPlayedToday = 0,
            workoutsToday = 0,
            gamesPlayedToday = 0,
            morningDoneDate = "",
            eveningDoneDate = ""
        )
        onStateChanged()
        syncLeaderboardToFirebase()
    }

    fun recordGamePlayed(gameKey: String, onStateChanged: () -> Unit = {}) {
        val today = getTodayDateString()
        val profileVal = _profile.value
        val isNewDay = profileVal.lastWorkoutDate != today

        _profile.value = profileVal.copy(
            lastWorkoutDate = today,
            slagalicaPlayedToday = if (isNewDay) (if (gameKey == "slagalica") 1 else 0) else (profileVal.slagalicaPlayedToday + (if (gameKey == "slagalica") 1 else 0)),
            mojbrojPlayedToday = if (isNewDay) (if (gameKey == "mojbroj") 1 else 0) else (profileVal.mojbrojPlayedToday + (if (gameKey == "mojbroj") 1 else 0)),
            asocijacijePlayedToday = if (isNewDay) (if (gameKey == "asocijacije") 1 else 0) else (profileVal.asocijacijePlayedToday + (if (gameKey == "asocijacije") 1 else 0)),
            spojnicePlayedToday = if (isNewDay) (if (gameKey == "spojnice") 1 else 0) else (profileVal.spojnicePlayedToday + (if (gameKey == "spojnice") 1 else 0)),
            koznaznaPlayedToday = if (isNewDay) (if (gameKey == "koznazna") 1 else 0) else (profileVal.koznaznaPlayedToday + (if (gameKey == "koznazna") 1 else 0)),
            srbijaPlayedToday = if (isNewDay) (if (gameKey == "srbija") 1 else 0) else (profileVal.srbijaPlayedToday + (if (gameKey == "srbija") 1 else 0))
        )
        onStateChanged()
        syncLeaderboardToFirebase()
    }

    fun recordWorkoutCompleted(type: String) {
        val today = getTodayDateString()
        val currentWorkouts = if (_profile.value.lastWorkoutDate == today) _profile.value.workoutsToday + 1 else 1

        val newMorning = if (type == "morning") today else _profile.value.morningDoneDate
        val newEvening = if (type == "evening") today else _profile.value.eveningDoneDate

        _profile.value = _profile.value.copy(
            lastWorkoutDate = today,
            workoutsToday = currentWorkouts,
            morningDoneDate = newMorning,
            eveningDoneDate = newEvening
        )
        syncLeaderboardToFirebase()
    }

    fun unlockLifetimeAccess() {
        _profile.value = _profile.value.copy(isLifetimeUnlocked = true)
        syncLeaderboardToFirebase()
    }

    fun deleteAccountAndResetData(onResetDone: () -> Unit) {
        try {
            if (_profile.value.userId.isNotEmpty()) {
                firestore.collection("leaderboard")
                    .document(_profile.value.userId)
                    .delete()
            }
        } catch (e: Exception) {
            // Ignore offline errors
        }

        _profile.value = UserProfile()
        AppStateManager.resetPointsAndGames()
        onResetDone()
    }

    fun checkRemoteProStatus(onStatusChecked: (Boolean) -> Unit = {}) {
        if (_profile.value.userId.isEmpty()) return
        try {
            firestore.collection("leaderboard")
                .document(_profile.value.userId)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        val isPro = doc.getBoolean("isPro") ?: false
                        if (isPro && !_profile.value.isLifetimeUnlocked) {
                            _profile.value = _profile.value.copy(isLifetimeUnlocked = true)
                            onStatusChecked(true)
                        } else {
                            onStatusChecked(_profile.value.isLifetimeUnlocked)
                        }
                    } else {
                        onStatusChecked(_profile.value.isLifetimeUnlocked)
                    }
                }
                .addOnFailureListener {
                    onStatusChecked(_profile.value.isLifetimeUnlocked)
                }
        } catch (e: Exception) {
            onStatusChecked(_profile.value.isLifetimeUnlocked)
        }
    }

    fun syncLeaderboardToFirebase() {
        try {
            val userMap = mutableMapOf<String, Any>(
                "userId" to _profile.value.userId,
                "name" to _profile.value.userName,
                "points" to AppStateManager.state.value.points,
                "streakDays" to AppStateManager.state.value.streakDays,
                "rankTitle" to AppStateManager.state.value.userRankTitle,
                "lastUpdated" to System.currentTimeMillis()
            )
            userMap["isPro"] = _profile.value.isLifetimeUnlocked

            firestore.collection("leaderboard")
                .document(_profile.value.userId)
                .set(userMap, com.google.firebase.firestore.SetOptions.merge())
        } catch (e: Exception) {
            // Fallback if offline
        }
    }

    fun fetchLiveLeaderboard(onResult: (List<Competitor>) -> Unit) {
        try {
            firestore.collection("leaderboard")
                .orderBy("points", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .addOnSuccessListener { result ->
                    val list = mutableListOf<Competitor>()
                    for (doc in result) {
                        val name = doc.getString("name") ?: "Kvizolog"
                        val points = doc.getLong("points")?.toInt() ?: 0
                        val uid = doc.getString("userId") ?: ""
                        val isMe = uid == _profile.value.userId
                        list.add(Competitor(name, points, isUser = isMe))
                    }
                    if (list.isNotEmpty()) {
                        onResult(list)
                    } else {
                        onResult(emptyList())
                    }
                }
                .addOnFailureListener {
                    onResult(emptyList())
                }
        } catch (e: Exception) {
            onResult(emptyList())
        }
    }
}
