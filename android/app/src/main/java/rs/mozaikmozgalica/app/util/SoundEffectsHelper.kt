package rs.mozaikmozgalica.app.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.sin

/**
 * Premium Mathematically-Synthesized Sound Effects System.
 * Generates beautiful, warm, organic sounds (FM Synthesis & Harmonics)
 * instead of harsh system beeps, running completely offline with zero file dependencies.
 */
object SoundEffectsHelper {
    private val scope = CoroutineScope(Dispatchers.Default)
    private const val SAMPLE_RATE = 22050

    fun playCorrectDing() {
        scope.launch {
            try {
                val durationMs = 600
                val numSamples = (durationMs * SAMPLE_RATE) / 1000
                val samples = FloatArray(numSamples)

                // Option 2: Gentle Harp Pluck (C5 & G5 rolling)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    // C5 (523.25 Hz)
                    val env1 = exp(-7.0 * t)
                    val wave1 = 0.5 * sin(2.0 * Math.PI * 523.25 * t)
                    
                    // G5 (783.99 Hz) starts slightly delayed at 0.08s
                    var env2 = 0.0
                    var wave2 = 0.0
                    if (t > 0.08) {
                        val t2 = t - 0.08
                        env2 = exp(-7.0 * t2)
                        wave2 = 0.5 * sin(2.0 * Math.PI * 783.99 * t2)
                    }
                    
                    samples[i] = ((wave1 * env1) + (wave2 * env2)).toFloat()
                }

                playBuffer(samples)
            } catch (e: Exception) {
                // Fail-safe
            }
        }
    }

    fun playWrongBuzz() {
        scope.launch {
            try {
                val durationMs = 450
                val numSamples = (durationMs * SAMPLE_RATE) / 1000
                val samples = FloatArray(numSamples)

                // Dissonant low frequencies for a warm game-show buzz (C3 + C#3)
                val freq1 = 130.81
                val freq2 = 138.59

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val envelope = exp(-5.0 * t)

                    // Combination of low sine waves
                    val wave = (0.6 * sin(2.0 * Math.PI * freq1 * t) +
                                0.4 * sin(2.0 * Math.PI * freq2 * t))
                    
                    samples[i] = (wave * envelope).toFloat()
                }

                playBuffer(samples)
            } catch (e: Exception) {
                // Fail-safe
            }
        }
    }

    fun playClick() {
        scope.launch {
            try {
                val durationMs = 60
                val numSamples = (durationMs * SAMPLE_RATE) / 1000
                val samples = FloatArray(numSamples)

                // Warmer fundamental frequencies for a woodblock / hollow click sound (280Hz and 560Hz)
                val freq1 = 280.0
                val freq2 = 560.0

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val envelope = exp(-50.0 * t) // Fast warm decay

                    val wave = 0.7 * sin(2.0 * Math.PI * freq1 * t) + 0.3 * sin(2.0 * Math.PI * freq2 * t)
                    samples[i] = (wave * envelope * 0.4).toFloat()
                }

                playBuffer(samples)
            } catch (e: Exception) {
                // Fail-safe
            }
        }
    }

    fun playVictoryChime() {
        scope.launch {
            try {
                val durationMs = 800
                val numSamples = (durationMs * SAMPLE_RATE) / 1000
                val samples = FloatArray(numSamples)

                // Celebratory ascending notes: C5 (523.25), E5 (659.25), G5 (783.99), C6 (1046.50)
                val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
                val noteSpacing = 0.08 // 80ms delay between notes

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    var waveSum = 0.0
                    for (noteIdx in notes.indices) {
                        val noteDelay = noteIdx * noteSpacing
                        if (t > noteDelay) {
                            val tLocal = t - noteDelay
                            val envelope = exp(-12.0 * tLocal) // Quick elegant decay
                            
                            // Main tone + first harmonic overtone
                            val wave = sin(2.0 * Math.PI * notes[noteIdx] * tLocal) + 
                                       0.3 * sin(2.0 * Math.PI * (notes[noteIdx] * 2.0) * tLocal)
                            waveSum += wave * envelope * 0.35
                        }
                    }
                    samples[i] = waveSum.toFloat()
                }

                playBuffer(samples)
            } catch (e: Exception) {
                // Fail-safe
            }
        }
    }

    private fun playBuffer(samples: FloatArray) {
        val bufferSize = samples.size * 2 // 16-bit PCM = 2 bytes per sample
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(samples, 0, samples.size, AudioTrack.WRITE_BLOCKING)
        audioTrack.play()
        
        // Asynchronously release track resources after it finishes playing
        scope.launch {
            try {
                Thread.sleep(1500)
                audioTrack.stop()
                audioTrack.release()
            } catch (e: Exception) {}
        }
    }
}
