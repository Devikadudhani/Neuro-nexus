package com.runanywhere.kotlin_starter_example.model

data class VoiceFeatures(
    val averageAmplitude: Double,
    val zeroCrossingRate: Double,
    val speakingDurationSec: Double,
    // Linguistic features from backend
    val speechRateWpm: Double = 0.0,
    val lexicalDiversity: Double = 0.0,
    val fillerCount: Int = 0,
    val repetitionRatio: Double = 0.0,
    val avgWordLength: Double = 0.0,
    
    // Cognitive Analysis (Dementia Detection Layer)
    val riskScore: Int = 0,
    val riskLevel: String = "Normal",
    val detectedIssues: List<String> = emptyList()
)

data class VoiceTaskResult(
    val timestampMs: Long,
    val features: VoiceFeatures,
    val score: Int
)
