package com.runanywhere.kotlin_starter_example.data

import com.runanywhere.kotlin_starter_example.model.FaceFeatures
import com.runanywhere.kotlin_starter_example.model.VoiceFeatures

data class CognitiveAnalysisResult(
    val memoryScore: Float,
    val orientationScore: Float,
    val attentionScore: Float,
    val reasoningScore: Float,
    val languageScore: Float,
    val remarks: String
)

object AnalysisDataStore {
    var voiceData: VoiceFeatures? = null 
    var faceData: FaceFeatures? = null 
    var cognitiveData: CognitiveAnalysisResult? = null

    init {
        cognitiveData = CognitiveAnalysisResult(
            memoryScore = 88f,
            orientationScore = 92f,
            attentionScore = 79f,
            reasoningScore = 50f,
            languageScore = 77f,
            remarks = "Cognitive profile shows strong memory and orientation with good attention span. Reasoning ability is comparatively lower."
        )
    }
}
