package com.runanywhere.kotlin_starter_example.ui

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.runanywhere.kotlin_starter_example.audio.VoiceFeatureExtractor
import com.runanywhere.kotlin_starter_example.data.VoiceTaskLocalStore
import com.runanywhere.kotlin_starter_example.model.VoiceTaskResult
import com.runanywhere.kotlin_starter_example.tts.TtsController
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.public.extensions.VoiceAgent.VoiceSessionConfig
import com.runanywhere.sdk.public.extensions.VoiceAgent.VoiceSessionEvent
import com.runanywhere.sdk.public.extensions.streamVoiceSession
import com.runanywhere.kotlin_starter_example.data.AnalysisDataStore
import com.runanywhere.kotlin_starter_example.data.SpeechAnalysisResult
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

class VoiceTaskViewModel(app: Application) : AndroidViewModel(app) {

    private val store = VoiceTaskLocalStore(app)
    private val tts = TtsController(app)
    private val _uiState = MutableStateFlow(VoiceTaskUiState())
    val uiState = _uiState.asStateFlow()

    private var sessionJob: Job? = null
    private var timerJob: Job? = null
    private var isCapturing = false
    private val sampleRate = 16000
    private val maxTaskDuration = 20

    fun startTask() {
        if (_uiState.value.isRecording) return
// Explicit permission check to handle security requirements
        if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            _uiState.value = _uiState.value.copy(status = "Error: Permission Denied")
            return
        }
        isCapturing = true
        _uiState.value = _uiState.value.copy(
            isRecording = true,
            isCompleted = false,
            status = "Recording...",
            transcript = "",
            timerSeconds = maxTaskDuration,
            result = null
        )

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            for (i in maxTaskDuration downTo 0) {
                _uiState.value = _uiState.value.copy(timerSeconds = i)
                if (i == 0) {
                    stopTaskGracefully()
                    break
                }
                delay(1000)
            }
        }

        val audioFlow = callbackFlow {
            if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                close(SecurityException("Recording permission not granted"))
                return@callbackFlow
            }
            val bufferSize = AudioRecord.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
                close(IllegalStateException("AudioRecord failed"))
                return@callbackFlow
            }

            audioRecord.startRecording()

            val readJob = launch(Dispatchers.IO) {
                val buffer = ShortArray(bufferSize / 2)
                val byteBuffer = ByteArray(bufferSize)
                try {
                    while (isActive && isCapturing) {
                        val read = audioRecord.read(buffer, 0, buffer.size)
                        if (read > 0) {
                            for (i in 0 until read) {
                                byteBuffer[i * 2] = (buffer[i].toInt() and 0x00FF).toByte()
                                byteBuffer[i * 2 + 1] = ((buffer[i].toInt() shr 8) and 0x00FF).toByte()
                            }
                            trySend(byteBuffer.copyOf(read * 2))
                        } else if (read < 0) break
                    }
                } catch (e: Exception) {
                    Log.e("VoiceTask", "Audio read error", e)
                } finally {
                    this@callbackFlow.close()
                }
            }

            awaitClose {
                isCapturing = false
                readJob.cancel()
                try {
                    if (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) audioRecord.stop()
                    audioRecord.release()
                } catch (e: Exception) {}
            }
        }

        val config = VoiceSessionConfig(
            silenceDuration = 10.0,
            continuousMode = false,
            autoPlayTTS = false
        )

        sessionJob = viewModelScope.launch {
            try {
                RunAnywhere.streamVoiceSession(audioFlow, config).collect { event ->
                    when (event) {
                        is VoiceSessionEvent.Transcribed -> {
                            _uiState.value = _uiState.value.copy(transcript = event.text)
                        }
                        is VoiceSessionEvent.TurnCompleted -> {
                            processFinalResult(_uiState.value.transcript, event.audio)
                        }
                        is VoiceSessionEvent.Error -> {
                            _uiState.value = _uiState.value.copy(status = "Error: ${event.message}", isRecording = false)
                            timerJob?.cancel()
                        }
                        else -> {}
                    }
                }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Log.e("VoiceTask", "Session error: ${e.message}")
                }
            }
        }
    }

    private fun stopTaskGracefully() {
        if (!isCapturing) return
        isCapturing = false
        timerJob?.cancel()
        timerJob = null
        _uiState.value = _uiState.value.copy(
            isRecording = false,
            status = "Finalizing results..."
        )
    }

    private fun processFinalResult(text: String, audioData: ByteArray?) {
        viewModelScope.launch {
            try {
                val samples = audioData?.let { bytes ->
                    ShortArray(bytes.size / 2) { i ->
                        ((bytes[i * 2].toInt() and 0xFF) or (bytes[i * 2 + 1].toInt() shl 8)).toShort()
                    }
                } ?: ShortArray(0)

                // Pass the transcript text to the extractor to calculate linguistic features
                val features = VoiceFeatureExtractor.extract(samples, sampleRate, text)
                val score = VoiceFeatureExtractor.score(features)

                val result = VoiceTaskResult(System.currentTimeMillis(), features, score)
                store.save(result)

                // Store in Global AnalysisDataStore for Reports
                AnalysisDataStore.speechData = SpeechAnalysisResult(
                    pitchScore = 80f, // Simplified for now
                    toneScore = 85f,
                    speechRate = features.speechRateWpm.toInt(),
                    clarityScore = (features.zeroCrossingRate * 500).toFloat().coerceIn(0f, 100f),
                    pauseDuration = (20 - features.speakingDurationSec).toFloat().coerceIn(0f, 20f),
                    remarks = "Analysis shows ${if(features.zeroCrossingRate > 0.1) "clear" else "muffled"} articulation with a speed of ${features.speechRateWpm.toInt()} WPM."
                )

                _uiState.value = _uiState.value.copy(
                    isRecording = false,
                    isCompleted = true,
                    status = "Task Completed",
                    result = result
                )

                tts.speak("Your score is $score.")
            } catch (e: Exception) {
                Log.e("VoiceTask", "Error in processFinalResult", e)
                _uiState.value = _uiState.value.copy(isRecording = false, status = "Processing Error")
            }
        }
    }

    fun stopTask() {
        stopTaskGracefully()
    }

    fun resetTask() {
//        _uiState.value = _uiState.value.copy(
//            isRecording = false,
//            isCompleted = false,
//            status = "Tap start and speak...",
//            transcript = "",
//            result = null,
//            timerSeconds = 20
//        )
        _uiState.value = VoiceTaskUiState()
    }

    override fun onCleared() {
        stopTask()
        tts.shutdown()
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return VoiceTaskViewModel(app) as T
        }
    }
}