package rs.mozaikmozgalica.app.data

enum class ScriptType {
    CIRILICA, LATINICA
}

enum class SeniorFontSize(val scale: Float, val label: String) {
    NORMAL(1.0f, "1x"),
    LARGE(1.25f, "1.25x"),
    XLARGE(1.5f, "1.5x")
}

data class TriviaQuestion(
    val id: Int,
    val category: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val funFact: String
)

data class AssociationGrid(
    val id: Int,
    val colAClues: List<String>,
    val colASolution: String,
    val colBClues: List<String>,
    val colBSolution: String,
    val colCClues: List<String>,
    val colCSolution: String,
    val colDClues: List<String>,
    val colDSolution: String,
    val finalSolution: String
)

data class SpojnicePair(
    val left: String,
    val right: String
)

data class SpojniceRound(
    val id: Int,
    val title: String,
    val pairs: List<SpojnicePair>
)

data class Competitor(
    val name: String,
    val points: Int,
    val isUser: Boolean = false,
    val iconName: String = "person"
)
