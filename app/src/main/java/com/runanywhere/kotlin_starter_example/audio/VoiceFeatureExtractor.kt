package com.runanywhere.kotlin_starter_example.audio

import com.runanywhere.kotlin_starter_example.model.VoiceFeatures
import kotlin.math.abs

object VoiceFeatureExtractor {

    fun extract(samples: ShortArray, sampleRate: Int, transcript: String): VoiceFeatures {
        val norm = Short.MAX_VALUE.toDouble()
        var sum = 0.0
        var zeroCross = 0
        var voiced = 0

        // Acoustic analysis
        for (i in samples.indices) {
            val v = abs(samples[i] / norm)
            sum += v
            if (v > 0.02) voiced++

            if (i > 0) {
                if ((samples[i - 1] >= 0 && samples[i] < 0) ||
                    (samples[i - 1] < 0 && samples[i] >= 0)
                ) zeroCross++
            }
        }

        val durationSec = voiced.toDouble() / sampleRate
        val durationMin = durationSec / 60.0
        val amplitude = sum / samples.size
        val clarity = zeroCross.toDouble() / samples.size

        // Linguistic analysis
        val words = transcript.lowercase().split(Regex("\\s+")).filter { it.isNotBlank() }
        val totalWords = words.size
        
        val speechRate = if (durationMin > 0) totalWords / durationMin else 0.0
        val uniqueWords = words.toSet().size
        val lexicalDiversity = if (totalWords > 0) uniqueWords.toDouble() / totalWords else 0.0
        val fillerWords = listOf("um", "uh", "like", "you know")
        val fillerCount = words.count { it in fillerWords }
        val repetitionRatio = if (totalWords > 0) 1.0 - (uniqueWords.toDouble() / totalWords) else 0.0
        val avgWordLength = if (totalWords > 0) words.sumOf { it.length }.toDouble() / totalWords else 0.0

        // --- NEW COGNITIVE ANALYSIS LAYER ---
        val issues = mutableListOf<String>()
        var rScore = 0

        if (clarity < 0.14) {
            issues.add("Detected patterns of hesitation")
            rScore += 2
        }
        if (repetitionRatio > 0.4) {
            issues.add("Frequent word repetition detected")
            rScore += 2
        }
        if (lexicalDiversity < 0.5 && repetitionRatio > 0.4 && speechRate > 180) {
            issues.add("Signs of cognitive confusion in speech patterns")
            rScore += 3
        }
        if (speechRate > 200 || (speechRate < 100 && totalWords > 0)) {
            issues.add("Abnormal speech velocity")
            rScore += 1
        }
        if (amplitude < 0.05) {
            issues.add("Lowered vocal energy levels")
            rScore += 1
        }

        val rLevel = when {
            rScore >= 5 -> "High Risk Pattern"
            rScore >= 2 -> "Mild Cognitive Concern"
            else -> "Normal"
        }

        return VoiceFeatures(
            averageAmplitude = amplitude,
            zeroCrossingRate = clarity,
            speakingDurationSec = durationSec,
            speechRateWpm = speechRate,
            lexicalDiversity = lexicalDiversity,
            fillerCount = fillerCount,
            repetitionRatio = repetitionRatio,
            avgWordLength = avgWordLength,
            riskScore = rScore,
            riskLevel = rLevel,
            detectedIssues = issues
        )
    }

    fun score(f: VoiceFeatures): Int {
        var finalScore = 100
        if (f.speechRateWpm < 90) finalScore -= 20
        else if (f.speechRateWpm < 110) finalScore -= 10
        if (f.lexicalDiversity < 0.4) finalScore -= 20
        else if (f.lexicalDiversity < 0.5) finalScore -= 10
        if (f.fillerCount > 3) finalScore -= 10
        if (f.repetitionRatio > 0.5) finalScore -= 10
        if (f.avgWordLength < 3.0) finalScore -= 10
        return finalScore.coerceIn(0, 100)
    }
}
