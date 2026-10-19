package rs.mozaikmozgalica.app.util

import android.content.Context
import android.speech.tts.TextToSpeech
import rs.mozaikmozgalica.app.data.ScriptType
import rs.mozaikmozgalica.app.data.SerbianTransliteration
import java.util.Locale

class TextToSpeechHelper(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(context, this)
    private var isReady = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("sr", "RS"))
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isReady = true
                tts?.setSpeechRate(0.85f)
            }
        }
    }

    fun speak(text: String, scriptType: ScriptType) {
        if (isReady && tts != null) {
            val textToSpeak = SerbianTransliteration.applyScript(text, scriptType)
            tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "MozaikTTS")
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
