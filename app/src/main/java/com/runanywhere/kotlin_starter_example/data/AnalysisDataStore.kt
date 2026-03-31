package com.runanywhere.kotlin_starter_example.data

data class SpeechAnalysisResult(
    val pitchScore: Float,
    val toneScore: Float,
    val speechRate: Int,
    val clarityScore: Float,
    val pauseDuration: Float,
    val remarks: String
)

data class FaceAnalysisResult(
    val eyebrowScore: Float,
    val mouthOpenness: Float,
    val browMovement: Float,
    val blinkRate: Int,
    val headMovement: Float,
    val reactionTime: Long,
    val remarks: String
)

data class CognitiveAnalysisResult(
    val memoryScore: Float,
    val orientationScore: Float,
    val attentionScore: Float,
    val reasoningScore: Float,
    val languageScore: Float,
    val remarks: String
)

object AnalysisDataStore {
    var speechData: SpeechAnalysisResult? = null
    var faceData: FaceAnalysisResult? = null
    var cognitiveData: CognitiveAnalysisResult? = null

    init {
        // Initialize with Mock Data for testing
        speechData = SpeechAnalysisResult(
            pitchScore = 88f,
            toneScore = 92f,
            speechRate = 120,
            clarityScore = 95f,
            pauseDuration = 6f,
            remarks = "Speech shows clear articulation, stable pitch, and expressive tone, indicating preserved ability."
        )
        faceData = FaceAnalysisResult(
            eyebrowScore = 88f,
            mouthOpenness = 92f,
            browMovement = 3f,
            blinkRate = 12,
            headMovement = 85f,
            reactionTime = 1200L,
            remarks = "Facial cues show active engagement with frequent nodding and high mouth openness."
        )
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
