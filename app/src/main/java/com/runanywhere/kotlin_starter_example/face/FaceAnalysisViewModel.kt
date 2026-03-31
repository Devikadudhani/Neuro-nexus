package com.runanywhere.kotlin_starter_example.face

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.runanywhere.kotlin_starter_example.data.AnalysisDataStore
import com.runanywhere.kotlin_starter_example.data.FaceAnalysisResult
import com.runanywhere.kotlin_starter_example.model.FaceFeatures
import com.runanywhere.kotlin_starter_example.model.FaceLandmarks
import com.runanywhere.kotlin_starter_example.model.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

data class FaceAnalysisUiState(
    val isTestRunning: Boolean = false,
    val status: String = "Align your face and tap Start",
    val blinkCount: Int = 0,
    val reactionTimeMs: Long? = null,
    val result: FaceFeatures? = null,
    val currentPrompt: String? = null
)

class FaceAnalysisViewModel(application: Application) : AndroidViewModel(application) {
    private val processor = FaceMeshProcessor(application)
    private val _uiState = MutableStateFlow(FaceAnalysisUiState())
    val uiState = _uiState.asStateFlow()

    private var startTime: Long = 0
    private var blinkDetected = AtomicBoolean(false)
    private var earHistory = mutableListOf<Float>()
    private var mouthMovements = mutableListOf<Float>()
    private var headMovements = mutableListOf<Point>()
    
    private var reactionStartTime: Long = 0
    private var isWaitingForReaction = false

    fun startTest() {
        _uiState.value = FaceAnalysisUiState(
            isTestRunning = true,
            status = "Monitoring patterns..."
        )
        startTime = System.currentTimeMillis()
        earHistory.clear()
        mouthMovements.clear()
        headMovements.clear()
        
        // Trigger reaction test after 5 seconds
        viewModelScope.launch {
            kotlinx.coroutines.delay(5000)
            triggerReactionTest()
        }
        
        // Stop test after 15 seconds
        viewModelScope.launch {
            kotlinx.coroutines.delay(15000)
            stopTest()
        }
    }

    private fun triggerReactionTest() {
        _uiState.value = _uiState.value.copy(
            currentPrompt = "SMILE NOW!",
            status = "Waiting for reaction..."
        )
        reactionStartTime = System.currentTimeMillis()
        isWaitingForReaction = true
    }

    fun onFrame(bitmap: Bitmap) {
        if (!_uiState.value.isTestRunning) return

        viewModelScope.launch(Dispatchers.Default) {
            val landmarks = processor.processFrame(bitmap) ?: return@launch
            
            val ear = (FaceFeatureExtractor.calculateEAR(landmarks.leftEye) + 
                      FaceFeatureExtractor.calculateEAR(landmarks.rightEye)) / 2f
            val mar = FaceFeatureExtractor.calculateMAR(landmarks.lips)
            
            // Blink detection
            if (ear < 0.2f && !blinkDetected.get()) {
                blinkDetected.set(true)
                _uiState.value = _uiState.value.copy(blinkCount = _uiState.value.blinkCount + 1)
            } else if (ear > 0.25f) {
                blinkDetected.set(false)
            }

            // Reaction detection
            if (isWaitingForReaction && mar > 0.5f) { // Simple threshold for smile/open mouth
                val reactionTime = System.currentTimeMillis() - reactionStartTime
                isWaitingForReaction = false
                _uiState.value = _uiState.value.copy(
                    reactionTimeMs = reactionTime,
                    currentPrompt = "Detected!",
                    status = "Continuing analysis..."
                )
            }

            earHistory.add(ear)
            mouthMovements.add(mar)
            headMovements.add(landmarks.noseTip)
        }
    }

    private fun stopTest() {
        viewModelScope.launch {
            val durationMin = (System.currentTimeMillis() - startTime) / 60000f
            val blinkRate = _uiState.value.blinkCount / (if (durationMin > 0) durationMin else 1f)
            
            // Calculate variance for mask-like face detection
            val marMean = if (mouthMovements.isNotEmpty()) mouthMovements.average().toFloat() else 0f
            val marVariance = if (mouthMovements.isNotEmpty()) {
                mouthMovements.map { (it - marMean) * (it - marMean) }.average().toFloat()
            } else 0f
            
            // Calculate head movement intensity
            var totalHeadMove = 0f
            for (i in 1 until headMovements.size) {
                val p1 = headMovements[i-1]
                val p2 = headMovements[i]
                totalHeadMove += Math.sqrt(
                    Math.pow((p1.x - p2.x).toDouble(), 2.0) + 
                    Math.pow((p1.y - p2.y).toDouble(), 2.0)
                ).toFloat()
            }

            val headStabilityScore = (100 - (totalHeadMove * 100).toInt()).coerceIn(0, 100)

            val features = FaceDementiaAnalyzer.analyze(
                blinkRateBpm = blinkRate,
                facialActivityLow = marVariance < 0.001f,
                headStabilityScore = headStabilityScore,
                reactionTimeMs = _uiState.value.reactionTimeMs
            )

            // Store results globally
            AnalysisDataStore.faceData = FaceAnalysisResult(
                eyebrowScore = 80f, // Placeholder
                mouthOpenness = (marMean * 100).coerceIn(0f, 100f),
                browMovement = 5f, // Placeholder
                blinkRate = blinkRate.toInt(),
                headMovement = (headStabilityScore).toFloat(),
                reactionTime = _uiState.value.reactionTimeMs ?: 0L,
                remarks = "Face analysis results stored on ${java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())}. Risk level: ${features.riskLevel}"
            )

            _uiState.value = _uiState.value.copy(
                isTestRunning = false,
                status = "Test Completed",
                result = features,
                currentPrompt = null
            )
        }
    }
}
