package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Spiritual harmonic sound synthesizer providing real-time 432Hz, 528Hz,
 * singing bowl harmonics, and gentle ambient soundscapes for contemplation and meditation.
 */
class SpiritualAudioEngine {

    private var audioTrack: AudioTrack? = null
    private var synthJob: Job? = null
    private val synthScope = CoroutineScope(Dispatchers.Default)

    @Volatile
    var isPlaying: Boolean = false
        private set

    @Volatile
    var currentSoundId: String = "432hz"
        private set

    @Volatile
    var volume: Float = 0.5f

    fun startSound(soundId: String, freqHz: Float = 432f) {
        stopSound()
        currentSoundId = soundId
        isPlaying = true

        synthJob = synthScope.launch {
            val sampleRate = 44100
            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(sampleRate / 4)

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack = track
            track.play()

            val samples = ShortArray(bufferSize / 2)
            var phase1 = 0.0
            var phase2 = 0.0
            var phase3 = 0.0
            var lfoPhase = 0.0

            val f1 = freqHz.toDouble()
            val f2 = f1 * 1.5 // 5th harmonic
            val f3 = f1 * 2.0 // Octave

            var lastNoise = 0.0

            try {
                while (isActive && isPlaying) {
                    for (i in samples.indices) {
                        val currentVol = volume.toDouble()

                        val sampleValue = when (soundId) {
                            "singing_bowl" -> {
                                // Singing bowl with warm pulsating tremolo
                                val tremolo = 0.85 + 0.15 * sin(lfoPhase * 2 * PI)
                                val s1 = sin(phase1 * 2 * PI) * 0.6
                                val s2 = sin(phase2 * 2 * PI) * 0.25
                                val s3 = sin(phase3 * 2 * PI) * 0.15
                                (s1 + s2 + s3) * tremolo * currentVol
                            }
                            "stream_ambient" -> {
                                // Filtered soft pink/nature noise
                                val white = Random.nextDouble(-1.0, 1.0)
                                val pink = (lastNoise * 0.95) + (white * 0.05)
                                lastNoise = pink
                                val waveMod = 0.7 + 0.3 * sin(lfoPhase * 2 * PI * 0.2)
                                pink * waveMod * currentVol * 0.45
                            }
                            "528hz" -> {
                                // 528Hz Solfeggio Transformation harmonic
                                val s1 = sin(phase1 * 2 * PI) * 0.7
                                val s2 = sin(phase2 * 2 * PI) * 0.2
                                val tremolo = 0.9 + 0.1 * sin(lfoPhase * 2 * PI * 0.5)
                                (s1 + s2) * tremolo * currentVol
                            }
                            "om_resonance" -> {
                                // Deep 136.1Hz Om frequency with rich sub-harmonics
                                val s1 = sin(phase1 * 2 * PI) * 0.65
                                val s2 = sin(phase2 * 2 * PI) * 0.2
                                val s3 = sin(phase1 * 0.5 * 2 * PI) * 0.15
                                val tremolo = 0.8 + 0.2 * sin(lfoPhase * 2 * PI * 0.25)
                                (s1 + s2 + s3) * tremolo * currentVol
                            }
                            else -> {
                                // 432Hz Calm Harmonic
                                val s1 = sin(phase1 * 2 * PI) * 0.7
                                val s2 = sin(phase2 * 2 * PI) * 0.2
                                val s3 = sin(phase3 * 2 * PI) * 0.1
                                (s1 + s2 + s3) * currentVol
                            }
                        }

                        // Convert to 16-bit PCM Short
                        val pcm = (sampleValue.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
                        samples[i] = pcm

                        // Increment phases
                        phase1 = (phase1 + f1 / sampleRate) % 1.0
                        phase2 = (phase2 + f2 / sampleRate) % 1.0
                        phase3 = (phase3 + f3 / sampleRate) % 1.0
                        lfoPhase = (lfoPhase + 0.1 / sampleRate) % 1.0
                    }

                    track.write(samples, 0, samples.size)
                }
            } catch (e: Exception) {
                // Audio synthesis ended
            } finally {
                try {
                    track.stop()
                    track.release()
                } catch (ignored: Exception) {}
            }
        }
    }

    fun playChimeGong() {
        synthScope.launch {
            val sampleRate = 44100
            val durationSec = 3.5
            val totalSamples = (sampleRate * durationSec).toInt()
            val samples = ShortArray(totalSamples)

            val f0 = 587.33 // D5 meditative gong tone
            val f1 = 880.00
            val f2 = 1174.66

            for (i in 0 until totalSamples) {
                val t = i.toDouble() / sampleRate
                val envelope = Math.exp(-t * 1.5) // Exponential decay
                val s = sin(2 * PI * f0 * t) * 0.6 +
                        sin(2 * PI * f1 * t) * 0.25 +
                        sin(2 * PI * f2 * t) * 0.15

                val sampleVal = (s * envelope * 0.7).coerceIn(-1.0, 1.0)
                samples[i] = (sampleVal * Short.MAX_VALUE).toInt().toShort()
            }

            try {
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(samples.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(samples, 0, samples.size)
                track.play()
            } catch (ignored: Exception) {}
        }
    }

    fun stopSound() {
        isPlaying = false
        synthJob?.cancel()
        synthJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (ignored: Exception) {}
        audioTrack = null
    }

    fun release() {
        stopSound()
    }
}
