package com.runanywhere.kotlin_starter_example.face

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.runanywhere.kotlin_starter_example.data.AnalysisDataStore
import com.runanywhere.kotlin_starter_example.model.*
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.public.extensions.chat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.pow
import kotlin.math.sqrt

data class FaceAnalysisUiState(
    val isTestRunning: Boolean = false,
    val status: String = "Align your face and tap Start",
    val result: FaceFeatures? = null,
    val currentPrompt: String? = null,
    val isSmiling: Boolean = false
)

class FaceAnalysisViewModel(application: Application) : AndroidViewModel(application) {
    private val processor = FaceMeshProcessor(application)
    private val _uiState = MutableStateFlow(FaceAnalysisUiState())
    val uiState = _uiState.asStateFlow()

    private var startTime: Long = 0
    private var blinkCount = 0
    private var blinkDetected = AtomicBoolean(false)
    
    private val dataMutex = Mutex()
    private val earHistory = mutableListOf<Float>()
    private val smileHistory = mutableListOf<Float>()
    private val mouthHistory = mutableListOf<Float>()
    private val browHistory = mutableListOf<Float>()
    private val yawHistory = mutableListOf<Float>()
    private val pitchHistory = mutableListOf<Float>()
    private val landmarkHistory = mutableListOf<List<Point>>()

    fun startTest() {
        _uiState.value = FaceAnalysisUiState(isTestRunning = true, status = "Monitoring facial cues...")
        startTime = System.currentTimeMillis()
        blinkCount = 0
        
        viewModelScope.launch {
            dataMutex.withLock {
                earHistory.clear()
                smileHistory.clear()
                mouthHistory.clear()
                browHistory.clear()
                yawHistory.clear()
                pitchHistory.clear()
                landmarkHistory.clear()
            }
            
            launch {
                val prompts = listOf(
                    "Look straight at the camera",
                    "Blink naturally",
                    "Now, give us a big smile!",
                    "Hold a neutral expression",
                    "Almost done, keep looking here"
                )
                prompts.forEach { prompt ->
                    if (_uiState.value.isTestRunning) {
                        _uiState.value = _uiState.value.copy(currentPrompt = prompt)
                        delay(3000)
                    }
                }
            }

            delay(15000)
            stopTest()
        }
    }

    fun onFrame(bitmap: Bitmap) {
        if (!_uiState.value.isTestRunning) return

        viewModelScope.launch(Dispatchers.Default) {
            val landmarks = try {
                processor.processFrame(bitmap)
            } catch (e: Exception) {
                null
            } ?: return@launch

            if (landmarks.ear < 0.2f && !blinkDetected.get()) {
                blinkDetected.set(true)
                blinkCount++
            } else if (landmarks.ear > 0.25f) {
                blinkDetected.set(false)
            }

            val isSmilingNow = landmarks.smileScore > 0.55f
            if (_uiState.value.isSmiling != isSmilingNow) {
                _uiState.value = _uiState.value.copy(isSmiling = isSmilingNow)
            }

            dataMutex.withLock {
                earHistory.add(landmarks.ear)
                smileHistory.add(landmarks.smileScore)
                mouthHistory.add(landmarks.mouthOpenness)
                browHistory.add(landmarks.browHeight)
                yawHistory.add(landmarks.yaw)
                pitchHistory.add(landmarks.pitch)
                
                if (System.currentTimeMillis() % 100 < 33) {
                    val allPoints = landmarks.leftEye + landmarks.rightEye + landmarks.lips + landmarks.leftEyebrow + landmarks.rightEyebrow
                    landmarkHistory.add(allPoints)
                }
            }
        }
    }

    private fun stopTest() {
        viewModelScope.launch {
            val durationSec = (System.currentTimeMillis() - startTime) / 1000f
            val durationMin = durationSec / 60f
            
            val bpm = if (durationMin > 0) blinkCount / durationMin else 0f
            
            val snapshot = dataMutex.withLock {
                DataSnapshot(
                    smileHistory = ArrayList(smileHistory),
                    mouthHistory = ArrayList(mouthHistory),
                    browHistory = ArrayList(browHistory),
                    yawHistory = ArrayList(yawHistory),
                    pitchHistory = ArrayList(pitchHistory),
                    landmarkHistory = ArrayList(landmarkHistory)
                )
            }

            var smileEvents = 0
            var inSmile = false
            for (score in snapshot.smileHistory) {
                if (score > 0.55f && !inSmile) {
                    smileEvents++
                    inSmile = true
                } else if (score < 0.45f) {
                    inSmile = false
                }
            }
            val smileRate = if (durationMin > 0) smileEvents / durationMin else 0f
            val avgMouth = if (snapshot.mouthHistory.isNotEmpty()) snapshot.mouthHistory.average().toFloat() else 0f
            val browStdDev = calculateStdDev(snapshot.browHistory)
            val timeLookingAwayRatio = if (snapshot.yawHistory.isNotEmpty()) {
                snapshot.yawHistory.count { Math.abs(it) > 0.4f }.toFloat() / snapshot.yawHistory.size
            } else 0f
            val pitchRange = if (snapshot.pitchHistory.isNotEmpty()) {
                (snapshot.pitchHistory.maxOrNull() ?: 0f) - (snapshot.pitchHistory.minOrNull() ?: 0f)
            } else 0f
            val yawRange = if (snapshot.yawHistory.isNotEmpty()) {
                (snapshot.yawHistory.maxOrNull() ?: 0f) - (snapshot.yawHistory.minOrNull() ?: 0f)
            } else 0f
            val varScore = calculateLandmarkVariability(snapshot.landmarkHistory)

            val features = FaceDementiaAnalyzer.analyze(
                blinkRateBpm = bpm,
                smileRatePerMin = smileRate,
                avgMouthOpenness = avgMouth,
                browStdDev = browStdDev,
                timeLookingAwayRatio = timeLookingAwayRatio,
                pitchRange = pitchRange,
                yawRange = yawRange,
                varScore = varScore
            )

            // --- LLM Insight Analysis ---
            val aiInsight = try {
                val prompt = """
                    You are a clinical AI assistant for NeuroNexus. Analyze these facial cues:
                    - Blink Rate: ${bpm.toInt()} BPM
                    - Smile Rate: $smileRate per min
                    - Mouth Mobility: $avgMouth
                    - Affect Variability: $varScore
                    - Detected Issues: ${features.detectedIssues.joinToString(", ")}
                    
                    Provide a concise (2-sentence) clinical insight about the user's emotional reactivity and motor control.
                """.trimIndent()
                RunAnywhere.chat(prompt)
            } catch (e: Exception) {
                null
            }

            val finalFeatures = features.copy(aiInsight = aiInsight)
            AnalysisDataStore.faceData = finalFeatures

            _uiState.value = _uiState.value.copy(
                isTestRunning = false,
                status = "Analysis Complete",
                result = finalFeatures,
                isSmiling = false,
                currentPrompt = null
            )
        }
    }

    private fun calculateStdDev(data: List<Float>): Float {
        if (data.isEmpty()) return 0f
        val mean = data.average().toFloat()
        return sqrt(data.map { (it - mean).pow(2) }.average()).toFloat()
    }

    private fun calculateLandmarkVariability(history: List<List<Point>>): Float {
        if (history.size < 2) return 0f
        var totalDisp = 0f
        var count = 0
        for (i in 1 until history.size) {
            val prev = history[i-1]
            val curr = history[i]
            if (prev.size == curr.size) {
                totalDisp += prev.zip(curr).map { (p1, p2) -> p1.distanceTo(p2) }.average().toFloat()
                count++
            }
        }
        return if (count > 0) totalDisp / count else 0f
    }

    private data class DataSnapshot(
        val smileHistory: List<Float>,
        val mouthHistory: List<Float>,
        val browHistory: List<Float>,
        val yawHistory: List<Float>,
        val pitchHistory: List<Float>,
        val landmarkHistory: List<List<Point>>
    )
}
