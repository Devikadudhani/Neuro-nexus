package com.runanywhere.kotlin_starter_example.audio

import com.runanywhere.kotlin_starter_example.model.VoiceFeatures
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.math.pow

object VoiceFeatureExtractor {

    private val fillerWords = setOf("um", "uh", "like", "you know", "well", "actually", "basically")
    private val pronouns = setOf("he", "she", "it", "they", "this", "that", "these", "those")
    private val functionWords = setOf(
        "the", "a", "an", "is", "am", "are", "was", "were", "be", "been", "being",
        "and", "but", "or", "for", "nor", "so", "yet", "to", "of", "in", "with",
        "at", "by", "from", "on", "into", "during", "including", "until", "against",
        "among", "throughout", "despite", "towards", "upon", "concerning", "about"
    )

    fun extract(samples: ShortArray, sampleRate: Int, transcript: String): VoiceFeatures {
        val norm = Short.MAX_VALUE.toDouble()
        var sum = 0.0
        var zeroCross = 0
        var voicedCount = 0
        val voicedThreshold = 0.02

        // 1. Acoustic Analysis
        for (i in samples.indices) {
            val v = abs(samples[i] / norm)
            sum += v
            if (v > voicedThreshold) voicedCount++
            if (i > 0 && ((samples[i - 1] >= 0 && samples[i] < 0) || (samples[i - 1] < 0 && samples[i] >= 0))) zeroCross++
        }

        val totalDurationSec = samples.size.toDouble() / sampleRate
        val speakingDurationSec = voicedCount.toDouble() / sampleRate
        val averageAmplitude = sum / samples.size
        val zeroCrossingRate = zeroCross.toDouble() / samples.size
        val rmsEnergy = sqrt(samples.map { (it / norm).let { v -> v * v } }.average())

        // 2. Pause Analysis (Big Upgrade)
        val silenceThresholdSamples = (sampleRate * 0.2).toInt() // 200ms
        var pauseCount = 0
        var totalPauseSamples = 0
        var silenceCounter = 0
        var longestPauseSamples = 0
        val pauses = mutableListOf<Int>()

        for (i in samples.indices) {
            if (abs(samples[i] / norm) < voicedThreshold) {
                silenceCounter++
            } else {
                if (silenceCounter >= silenceThresholdSamples) {
                    pauseCount++
                    totalPauseSamples += silenceCounter
                    pauses.add(silenceCounter)
                    if (silenceCounter > longestPauseSamples) longestPauseSamples = silenceCounter
                }
                silenceCounter = 0
            }
        }
        val meanPauseDuration = if (pauseCount > 0) (totalPauseSamples.toDouble() / pauseCount) / sampleRate else 0.0
        val pauseRate = if (totalDurationSec > 0) (pauseCount / totalDurationSec) * 60.0 else 0.0
        val longestPause = longestPauseSamples.toDouble() / sampleRate
        val pauseToSpeechRatio = if (speakingDurationSec > 0) (totalPauseSamples.toDouble() / sampleRate) / speakingDurationSec else 0.0

        // 3. Linguistic Analysis (Upgrade)
        val sentences = transcript.split(Regex("[.!?]+")).filter { it.isNotBlank() }
        val words = transcript.lowercase().split(Regex("\\s+")).filter { it.isNotBlank() }
        val totalWords = words.size
        
        val speechRateWpm = if (speakingDurationSec > 0) (totalWords / speakingDurationSec) * 60.0 else 0.0
        
        // MATTR (Moving Average Type-Token Ratio)
        val windowSize = 50
        val mattr = if (words.size >= windowSize) {
            words.windowed(windowSize, step = 1).map { win -> win.toSet().size.toDouble() / win.size }.average()
        } else {
            if (totalWords > 0) words.toSet().size.toDouble() / totalWords else 0.0
        }

        val contentWordsCount = words.count { it !in functionWords && it !in pronouns }
        val informationDensity = if (totalWords > 0) contentWordsCount.toDouble() / totalWords else 0.0
        val pronounRatio = if (totalWords > 0) words.count { it in pronouns }.toDouble() / totalWords else 0.0
        
        // Syntactic Complexity
        val avgSentenceLength = if (sentences.isNotEmpty()) totalWords.toDouble() / sentences.size else 0.0
        val sentenceLengthVariance = if (sentences.isNotEmpty()) {
            val lengths = sentences.map { it.trim().split("\\s+".toRegex()).size.toDouble() }
            val avg = lengths.average()
            lengths.map { (it - avg).pow(2.0) }.average()
        } else 0.0
        val shortSentenceRatio = if (sentences.isNotEmpty()) sentences.count { it.trim().split("\\s+".toRegex()).size < 5 }.toDouble() / sentences.size else 0.0

        // Phrase Repetition (Bigrams)
        val bigrams = words.zipWithNext { a, b -> "$a $b" }
        val phraseRepetitionRatio = if (bigrams.isNotEmpty()) (bigrams.size - bigrams.toSet().size).toDouble() / bigrams.size else 0.0

        // 4. Semantic Coherence
        val coherenceScore = if (sentences.size > 1) {
            sentences.zipWithNext { s1, s2 ->
                calculateCosineSimilarity(s1.lowercase().trim().split("\\s+".toRegex()), s2.lowercase().trim().split("\\s+".toRegex()))
            }.average()
        } else 1.0

        // Discourse
        val expectedTopics = setOf("kitchen", "cookie", "boy", "woman", "water", "sink", "stool")
        val topicCoverage = if (totalWords > 0) words.count { it in expectedTopics }.toDouble() / expectedTopics.size else 0.0
        val ideaDensity = if (totalWords > 0) contentWordsCount.toDouble() / (totalWords / 10.0).coerceAtLeast(1.0) else 0.0

        // 5. Tempo Variability
        val windowDur = 10.0 // 10 sec windows
        val samplesPerWindow = (sampleRate * windowDur).toInt()
        val tempoVariability = if (samples.size > samplesPerWindow) {
            val windowWpms = samples.toList().chunked(samplesPerWindow).map { win ->
                // This is a rough proxy since we don't have word timestamps here
                // We'll use energy distribution as a proxy for tempo variability
                val energy = win.map { abs(it / norm) }.average()
                energy
            }
            val avg = windowWpms.average()
            sqrt(windowWpms.map { (it - avg).pow(2.0) }.average())
        } else 0.0

        // Dementia Score Synthesis
        var rScore = 0
        if (pauseRate > 10) rScore += 3
        if (meanPauseDuration > 1.5) rScore += 2
        if (mattr < 0.5) rScore += 2
        if (informationDensity < 0.4) rScore += 2
        if (speechRateWpm < 90) rScore += 2
        if (coherenceScore < 0.3) rScore += 3

        val rLevel = when {
            rScore >= 7 -> "High Risk Pattern"
            rScore >= 3 -> "Mild Cognitive Concern"
            else -> "Normal"
        }

        return VoiceFeatures(
            averageAmplitude = averageAmplitude,
            zeroCrossingRate = zeroCrossingRate,
            speakingDurationSec = speakingDurationSec,
            speechRateWpm = speechRateWpm,
            lexicalDiversity = mattr,
            fillerCount = words.count { it in fillerWords },
            fillersPerMin = (words.count { it in fillerWords } / totalDurationSec.coerceAtLeast(1.0)) * 60.0,
            repetitionRatio = phraseRepetitionRatio,
            avgWordLength = if (totalWords > 0) words.sumOf { it.length }.toDouble() / totalWords else 0.0,
            
            // Advanced
            rmsEnergy = rmsEnergy,
            pauseCount = pauseCount,
            meanPauseDuration = meanPauseDuration,
            pauseRate = pauseRate,
            longestPause = longestPause,
            pauseToSpeechRatio = pauseToSpeechRatio,
            tempoVariability = tempoVariability,
            
            informationDensity = informationDensity,
            avgSentenceLength = avgSentenceLength,
            sentenceLengthVariance = sentenceLengthVariance,
            shortSentenceRatio = shortSentenceRatio,
            pronounRatio = pronounRatio,
            phraseRepetitionRatio = phraseRepetitionRatio,
            
            coherenceScore = coherenceScore,
            topicCoverage = topicCoverage,
            ideaDensity = ideaDensity,
            
            riskScore = rScore,
            riskLevel = rLevel,
            detectedIssues = buildDetectedIssues(pauseRate, meanPauseDuration, mattr, informationDensity, coherenceScore)
        )
    }

    private fun buildDetectedIssues(pauseRate: Double, pauseDur: Double, mattr: Double, infoDensity: Double, coherence: Double): List<String> {
        return mutableListOf<String>().apply {
            if (pauseRate > 10) add("Significant pause frequency")
            if (pauseDur > 1.5) add("Word-finding hesitations")
            if (mattr < 0.5) add("Repetitive vocabulary")
            if (infoDensity < 0.4) add("Low information density")
            if (coherence < 0.3) add("Fragmented narrative")
        }
    }

    private fun calculateCosineSimilarity(sent1: List<String>, sent2: List<String>): Double {
        val vocab = (sent1 + sent2).toSet()
        if (vocab.isEmpty()) return 0.0
        val vec1 = vocab.map { if (it in sent1) 1.0 else 0.0 }
        val vec2 = vocab.map { if (it in sent2) 1.0 else 0.0 }
        var dotProduct = 0.0
        for (i in vec1.indices) dotProduct += vec1[i] * vec2[i]
        val mag1 = sqrt(vec1.sumOf { it * it })
        val mag2 = sqrt(vec2.sumOf { it * it })
        return if (mag1 > 0 && mag2 > 0) dotProduct / (mag1 * mag2) else 0.0
    }

    fun score(f: VoiceFeatures): Int {
        var s = 100
        if (f.speechRateWpm < 90) s -= 20
        if (f.lexicalDiversity < 0.5) s -= 15
        if (f.pauseRate > 10) s -= 15
        if (f.coherenceScore < 0.4) s -= 20
        return s.coerceIn(0, 100)
    }
}
