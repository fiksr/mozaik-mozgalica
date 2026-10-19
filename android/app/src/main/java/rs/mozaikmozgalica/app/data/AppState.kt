package rs.mozaikmozgalica.app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppState(
    val points: Int = 0,
    val gamesPlayed: Int = 0,
    val streakDays: Int = 1,
    val scriptType: ScriptType = ScriptType.CIRILICA,
    val fontSize: SeniorFontSize = SeniorFontSize.NORMAL,
    val isMuted: Boolean = false,
    val userRankTitle: String = "Radoznali Um"
)

object AppStateManager {
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    var onStateChangedListener: ((points: Int, games: Int, streak: Int) -> Unit)? = null

    fun addPoints(pts: Int) {
        val newPts = _state.value.points + pts
        val newGames = _state.value.gamesPlayed + 1
        val newTitle = calculateTitle(newPts)
        _state.value = _state.value.copy(
            points = newPts,
            gamesPlayed = newGames,
            userRankTitle = newTitle
        )
        onStateChangedListener?.invoke(newPts, newGames, _state.value.streakDays)
    }

    fun restoreState(points: Int, games: Int, streak: Int) {
        _state.value = _state.value.copy(
            points = points,
            gamesPlayed = games,
            streakDays = streak,
            userRankTitle = calculateTitle(points)
        )
    }

    fun resetPointsAndGames() {
        _state.value = _state.value.copy(
            points = 0,
            gamesPlayed = 0,
            streakDays = 1,
            userRankTitle = "Radoznali Um"
        )
        onStateChangedListener?.invoke(0, 0, 1)
    }

    fun toggleScript() {
        val next = if (_state.value.scriptType == ScriptType.CIRILICA) ScriptType.LATINICA else ScriptType.CIRILICA
        _state.value = _state.value.copy(scriptType = next)
    }

    fun toggleFontSize() {
        val next = when (_state.value.fontSize) {
            SeniorFontSize.NORMAL -> SeniorFontSize.LARGE
            SeniorFontSize.LARGE -> SeniorFontSize.XLARGE
            SeniorFontSize.XLARGE -> SeniorFontSize.NORMAL
        }
        _state.value = _state.value.copy(fontSize = next)
    }

    fun toggleMute() {
        _state.value = _state.value.copy(isMuted = !_state.value.isMuted)
    }

    private fun calculateTitle(points: Int): String {
        return when {
            points >= 300 -> "Kviz Legenda"
            points >= 150 -> "Šampion Slagalice"
            points >= 50 -> "Majstor Mozgalica"
            else -> "Radoznali Um"
        }
    }
}
