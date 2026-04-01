package com.runanywhere.kotlin_starter_example.model

data class VoiceFeatures(
    val averageAmplitude: Double,
    val zeroCrossingRate: Double,
    val speakingDurationSec: Double,
    val speechRateWpm: Double = 0.0,
    val lexicalDiversity: Double = 0.0, // MATTR
    val fillerCount: Int = 0,
    val fillersPerMin: Double = 0.0,
    val repetitionRatio: Double = 0.0,
    val avgWordLength: Double = 0.0,
    
    // --- ADVANCED ACOUSTIC ---
    val meanF0: Double = 0.0,
    val f0Range: Double = 0.0,
    val f0Variability: Double = 0.0,
    val jitter: Double = 0.0,
    val shimmer: Double = 0.0,
    val rmsEnergy: Double = 0.0,
    val pauseCount: Int = 0,
    val meanPauseDuration: Double = 0.0,
    val pauseRate: Double = 0.0, 
    val longestPause: Double = 0.0,
    val pauseToSpeechRatio: Double = 0.0,
    val tempoVariability: Double = 0.0,
    
    // --- ADVANCED LINGUISTIC ---
    val informationDensity: Double = 0.0,
    val avgSentenceLength: Double = 0.0,
    val sentenceLengthVariance: Double = 0.0,
    val shortSentenceRatio: Double = 0.0,
    val pronounRatio: Double = 0.0,
    val wordFindingFailures: Int = 0,
    val phraseRepetitionRatio: Double = 0.0,
    
    // --- DISCOURSE ---
    val coherenceScore: Double = 0.0,
    val topicCoverage: Double = 0.0,
    val ideaDensity: Double = 0.0,
    
    // --- ANALYSIS ---
    val riskScore: Int = 0,
    val riskLevel: String = "Normal",
    val detectedIssues: List<String> = emptyList(),
    val aiInsight: String? = null
)

data class VoiceTaskResult(
    val timestampMs: Long,
    val features: VoiceFeatures,
    val score: Int
)
