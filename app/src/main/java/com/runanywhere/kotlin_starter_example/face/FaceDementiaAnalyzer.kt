package com.runanywhere.kotlin_starter_example.face

import com.runanywhere.kotlin_starter_example.model.FaceFeatures

object FaceDementiaAnalyzer {

    fun analyze(
        blinkRateBpm: Float,
        facialActivityLow: Boolean,
        headStabilityScore: Int,
        reactionTimeMs: Long?
    ): FaceFeatures {
        val issues = mutableListOf<String>()
        
        // 1. Reaction Time Scoring (35%)
        val (reactionScore, reactionCat) = when {
            reactionTimeMs == null -> 80 to "N/A"
            reactionTimeMs < 1500 -> 10 to "Fast"
            reactionTimeMs <= 3000 -> 40 to "Normal"
            else -> 80 to "Slow"
        }
        if (reactionCat == "Slow") issues.add("Slower than average facial reaction time")

        // 2. Blink Rate Scoring (25%)
        val (blinkScore, blinkCat) = when {
            blinkRateBpm in 8f..20f -> 20 to "Normal"
            blinkRateBpm < 8f -> 60 to "Low"
            else -> 50 to "High"
        }
        if (blinkCat == "Low") issues.add("Reduced spontaneous blink rate")

        // 3. Facial Activity Scoring (20%)
        val (activityScore, activityCat) = if (facialActivityLow) {
            70 to "Low Activity"
        } else {
            20 to "Normal"
        }
        if (facialActivityLow) issues.add("Detected reduced facial expressiveness")

        // 4. Head Stability Scoring (20%)
        val headCat = when {
            headStabilityScore >= 70 -> "Stable"
            headStabilityScore >= 40 -> "Slightly Unstable"
            else -> "Unstable"
        }
        val headScore = when (headCat) {
            "Stable" -> 20
            "Slightly Unstable" -> 50
            else -> 80
        }
        if (headCat == "Unstable") issues.add("Increased head movement instability")

        // Weighted Final Score
        val finalScore = (
            (reactionScore * 0.35f) +
            (blinkScore * 0.25f) +
            (activityScore * 0.20f) +
            (headScore * 0.20f)
        ).toInt()

        val riskLevel = when {
            finalScore <= 30 -> "Normal"
            finalScore <= 60 -> "Mild Cognitive Risk"
            else -> "High Cognitive Risk"
        }

        return FaceFeatures(
            blinkRateBpm = blinkRateBpm,
            blinkCategory = blinkCat,
            headStabilityScore = headStabilityScore,
            headCategory = headCat,
            reactionTimeMs = reactionTimeMs,
            reactionCategory = reactionCat,
            facialActivity = activityCat,
            riskScore = finalScore,
            riskLevel = riskLevel,
            detectedIssues = issues
        )
    }
}
