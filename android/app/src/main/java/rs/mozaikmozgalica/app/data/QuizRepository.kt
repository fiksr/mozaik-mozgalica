package rs.mozaikmozgalica.app.data

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONArray
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object QuizRepository {

    val slagalicaPresets = listOf(
        listOf("K", "O", "M", "U", "N", "I", "K", "A", "C", "I", "J", "A"),
        listOf("S", "L", "A", "G", "A", "L", "I", "C", "A", "M", "O", "Z"),
        listOf("P", "R", "I", "J", "A", "T", "E", "L", "J", "S", "T", "V"),
        listOf("B", "I", "S", "T", "R", "I", "N", "A", "Z", "D", "R", "A"),
        listOf("N", "E", "U", "R", "O", "P", "L", "A", "S", "T", "I", "K"),
        listOf("A", "S", "O", "C", "I", "J", "A", "C", "I", "J", "E", "S")
    )

    private val loadedAssocGrids = mutableListOf<AssociationGrid>()
    private val dynamicAssocGrids = mutableListOf<AssociationGrid>()
    private val dynamicKoZnaZna = mutableListOf<TriviaQuestion>()

    fun loadAssociationsFromAssets(context: Context): List<AssociationGrid> {
        // First check if cached remote JSON exists in app's internal storage
        try {
            val cacheFile = File(context.filesDir, "cached_asocijacije.json")
            if (cacheFile.exists()) {
                val jsonStr = cacheFile.readText(Charsets.UTF_8)
                val cached = parseAssociationsJson(jsonStr)
                if (cached.isNotEmpty()) {
                    dynamicAssocGrids.clear()
                    dynamicAssocGrids.addAll(cached)
                }
            }
        } catch (e: Exception) {
            // Fallback
        }

        // Also check if cached remote questions exist
        try {
            val cacheFileQ = File(context.filesDir, "cached_pitanja.json")
            if (cacheFileQ.exists()) {
                val jsonStr = cacheFileQ.readText(Charsets.UTF_8)
                val cachedQ = parseQuestionsJson(jsonStr)
                if (cachedQ.isNotEmpty()) {
                    dynamicKoZnaZna.clear()
                    dynamicKoZnaZna.addAll(cachedQ)
                }
            }
        } catch (e: Exception) {
            // Fallback
        }

        if (loadedAssocGrids.isNotEmpty()) return assocGrids

        try {
            val inputStream = context.assets.open("asocijacije.json")
            val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            val jsonStr = reader.use { it.readText() }
            val parsed = parseAssociationsJson(jsonStr)
            loadedAssocGrids.addAll(parsed)
        } catch (e: Exception) {
            // Fallback
        }

        return assocGrids
    }

    val assocGrids: List<AssociationGrid>
        get() = if (dynamicAssocGrids.isNotEmpty()) dynamicAssocGrids else (if (loadedAssocGrids.isNotEmpty()) loadedAssocGrids else fallbackAssocGrids)

    val koZnaZnaQuestions: List<TriviaQuestion>
        get() = if (dynamicKoZnaZna.isNotEmpty()) dynamicKoZnaZna else QuizContent.koZnaZnaQuestions

    val spojnicePool get() = QuizContent.spojnicePool
    val kolikoZnasSrbijuQuestions get() = QuizContent.kolikoZnasSrbijuQuestions

    /**
     * Sync remote JSON files drag-and-dropped into Firebase Storage / GitHub / Web host.
     * Checks Firestore document `quiz_content/config` for:
     * - `pitanja_url`
     * - `asocijacije_url`
     * Downloads them, parses, updates memory, and caches to disk for 100% offline access.
     */
    fun syncContentFromFirebase(context: Context? = null, onComplete: () -> Unit = {}) {
        try {
            val firestore = FirebaseFirestore.getInstance()

            // 1. Direct paste in quiz_content/koznazna -> json_data
            firestore.collection("quiz_content").document("koznazna").get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        val jsonStr = doc.getString("json_data")
                        if (!jsonStr.isNullOrEmpty()) {
                            val parsed = parseQuestionsJson(jsonStr)
                            if (parsed.isNotEmpty()) {
                                dynamicKoZnaZna.clear()
                                dynamicKoZnaZna.addAll(parsed)
                                if (context != null) {
                                    try {
                                        File(context.filesDir, "cached_pitanja.json").writeText(jsonStr, Charsets.UTF_8)
                                    } catch (e: Exception) {}
                                }
                            }
                        }
                    }
                }

            // 2. Direct paste in quiz_content/asocijacije -> json_data
            firestore.collection("quiz_content").document("asocijacije").get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        val jsonStr = doc.getString("json_data")
                        if (!jsonStr.isNullOrEmpty()) {
                            val parsed = parseAssociationsJson(jsonStr)
                            if (parsed.isNotEmpty()) {
                                dynamicAssocGrids.clear()
                                dynamicAssocGrids.addAll(parsed)
                                if (context != null) {
                                    try {
                                        File(context.filesDir, "cached_asocijacije.json").writeText(jsonStr, Charsets.UTF_8)
                                    } catch (e: Exception) {}
                                }
                            }
                        }
                    }
                }

            // 3. Fallback: remote URL config
            firestore.collection("quiz_content").document("config").get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        val pitanjaUrl = doc.getString("pitanja_url")
                        val asocijacijeUrl = doc.getString("asocijacije_url")

                        if (!pitanjaUrl.isNullOrEmpty()) {
                            Thread {
                                val jsonStr = downloadJson(pitanjaUrl)
                                if (!jsonStr.isNullOrEmpty()) {
                                    val parsed = parseQuestionsJson(jsonStr)
                                    if (parsed.isNotEmpty()) {
                                        dynamicKoZnaZna.clear()
                                        dynamicKoZnaZna.addAll(parsed)
                                        if (context != null) {
                                            try {
                                                File(context.filesDir, "cached_pitanja.json").writeText(jsonStr, Charsets.UTF_8)
                                            } catch (e: Exception) {}
                                        }
                                    }
                                }
                            }.start()
                        }

                        if (!asocijacijeUrl.isNullOrEmpty()) {
                            Thread {
                                val jsonStr = downloadJson(asocijacijeUrl)
                                if (!jsonStr.isNullOrEmpty()) {
                                    val parsed = parseAssociationsJson(jsonStr)
                                    if (parsed.isNotEmpty()) {
                                        dynamicAssocGrids.clear()
                                        dynamicAssocGrids.addAll(parsed)
                                        if (context != null) {
                                            try {
                                                File(context.filesDir, "cached_asocijacije.json").writeText(jsonStr, Charsets.UTF_8)
                                            } catch (e: Exception) {}
                                        }
                                    }
                                }
                            }.start()
                        }
                    }
                    onComplete()
                }
                .addOnFailureListener {
                    onComplete()
                }
        } catch (e: Exception) {
            onComplete()
        }
    }

    private fun downloadJson(urlString: String): String? {
        return try {
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 10000
            conn.readTimeout = 10000
            conn.requestMethod = "GET"
            if (conn.responseCode == 200) {
                conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun parseQuestionsJson(jsonStr: String): List<TriviaQuestion> {
        val list = mutableListOf<TriviaQuestion>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val id = obj.optInt("id", i + 1)
                val cat = obj.optString("category", "Opšte znanje")
                val q = obj.getString("question")
                val optionsArr = obj.getJSONArray("options")
                val options = mutableListOf<String>()
                for (j in 0 until optionsArr.length()) {
                    options.add(optionsArr.getString(j))
                }
                val cIdx = obj.optInt("correctIndex", 0)
                val ff = obj.optString("funFact", "")
                list.add(TriviaQuestion(id, cat, q, options, cIdx, ff))
            }
        } catch (e: Exception) {
            // Ignore
        }
        return list
    }

    fun parseAssociationsJson(jsonStr: String): List<AssociationGrid> {
        val list = mutableListOf<AssociationGrid>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val id = obj.optInt("id", i + 1)
                val finalSol = obj.getString("final")
                val colsArr = obj.getJSONArray("cols")
                val colA = colsArr.getJSONObject(0)
                val colB = colsArr.getJSONObject(1)
                val colC = colsArr.getJSONObject(2)
                val colD = colsArr.getJSONObject(3)

                list.add(
                    AssociationGrid(
                        id = id,
                        colAClues = jsonArrayToList(colA.getJSONArray("clues")),
                        colASolution = colA.getString("sol"),
                        colBClues = jsonArrayToList(colB.getJSONArray("clues")),
                        colBSolution = colB.getString("sol"),
                        colCClues = jsonArrayToList(colC.getJSONArray("clues")),
                        colCSolution = colC.getString("sol"),
                        colDClues = jsonArrayToList(colD.getJSONArray("clues")),
                        colDSolution = colD.getString("sol"),
                        finalSolution = finalSol
                    )
                )
            }
        } catch (e: Exception) {
            // Ignore
        }
        return list
    }

    private fun jsonArrayToList(arr: JSONArray): List<String> {
        val list = mutableListOf<String>()
        for (i in 0 until arr.length()) {
            list.add(arr.getString(i))
        }
        return list
    }

    private val fallbackAssocGrids = emptyList<AssociationGrid>()
}
