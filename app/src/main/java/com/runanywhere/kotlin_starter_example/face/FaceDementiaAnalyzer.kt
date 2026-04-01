package com.runanywhere.kotlin_starter_example.face

import com.runanywhere.kotlin_starter_example.model.FaceFeatures
import com.runanywhere.kotlin_starter_example.model.FeatureResult

object FaceDementiaAnalyzer {

    // ── Threshold Constants (Relaxed for real-world usage) ────────────────────

    private const val BLINK_VERY_LOW   = 8f
    private const val BLINK_LOW        = 12f
    private const val BLINK_HIGH       = 20f
    private const val BLINK_VERY_HIGH  = 30f

    private const val SMILE_VERY_LOW   = 1.0f   // events/min
    private const val SMILE_LOW        = 2.5f

    private const val MOUTH_LOW        = 0.015f

    private const val BROW_VERY_LOW    = 0.008f
    private const val BROW_LOW        = 0.018f

    // Gaze: Increased tolerance for off-center seating
    private const val GAZE_CONCERN    = 0.30f   // >30% frames = engagement concern
    private const val GAZE_HIGH       = 0.50f   // >50% frames = low engagement

    private const val PITCH_STATIC    = 0.02f   // More permissive
    private const val PITCH_HIGH      = 0.25f

    private const val VAR_VERY_LOW    = 0.005f
    private const val VAR_LOW         = 0.015f

    private const val W_BLINK   = 0.20f
    private const val W_EXPRESS = 0.20f
    private const val W_MOUTH   = 0.10f
    private const val W_BROW    = 0.15f
    private const val W_GAZE    = 0.15f
    private const val W_HEAD    = 0.05f
    private const val W_OVERALL = 0.15f

    private const val RED    = 0xFFFF4444L
    private const val AMBER  = 0xFFFFBB33L
    private const val GREEN  = 0xFF669900L

    fun analyze(
        blinkRateBpm: Float,
        smileRatePerMin: Float,
        avgMouthOpenness: Float,
        browStdDev: Float,
        timeLookingAwayRatio: Float,
        pitchRange: Float,
        yawRange: Float,
        varScore: Float
    ): FaceFeatures {

        val blinkRes = when {
            blinkRateBpm < BLINK_VERY_LOW  -> FeatureResult("Blink Rate",
                "Very Low (${blinkRateBpm.toInt()} BPM) — hypomimia pattern", RED)
            blinkRateBpm < BLINK_LOW       -> FeatureResult("Blink Rate",
                "Slightly Low (${blinkRateBpm.toInt()} BPM)", AMBER)
            blinkRateBpm <= BLINK_HIGH     -> FeatureResult("Blink Rate",
                "Normal (${blinkRateBpm.toInt()} BPM)", GREEN)
            blinkRateBpm <= BLINK_VERY_HIGH -> FeatureResult("Blink Rate",
                "Mildly Elevated (${blinkRateBpm.toInt()} BPM)", AMBER)
            else                           -> FeatureResult("Blink Rate",
                "Significantly Elevated (${blinkRateBpm.toInt()} BPM)", RED)
        }

        val expressRes = when {
            smileRatePerMin < SMILE_VERY_LOW -> FeatureResult("Expressiveness",
                "Very Low — masked affect pattern", RED)
            smileRatePerMin < SMILE_LOW      -> FeatureResult("Expressiveness",
                "Reduced", AMBER)
            else                             -> FeatureResult("Expressiveness",
                "Normal", GREEN)
        }

        val mouthRes = when {
            avgMouthOpenness < MOUTH_LOW -> FeatureResult("Mouth Mobility",
                "Reduced — limited speech movement detected", AMBER)
            else                         -> FeatureResult("Mouth Mobility",
                "Normal", GREEN)
        }

        val browRes = when {
            browStdDev < BROW_VERY_LOW -> FeatureResult("Facial Reactivity",
                "Very Low — flat affect", RED)
            browStdDev < BROW_LOW      -> FeatureResult("Facial Reactivity",
                "Reduced", AMBER)
            else                       -> FeatureResult("Facial Reactivity",
                "Normal", GREEN)
        }

        val gazeRes = when {
            timeLookingAwayRatio <= GAZE_CONCERN -> FeatureResult("Gaze Engagement",
                "Normal", GREEN)
            timeLookingAwayRatio <= GAZE_HIGH    -> FeatureResult("Gaze Engagement",
                "Reduced", AMBER)
            else                                 -> FeatureResult("Gaze Engagement",
                "Low — sustained gaze avoidance", RED)
        }

        val headRes = when {
            pitchRange < PITCH_STATIC && yawRange < PITCH_STATIC ->
                FeatureResult("Head Engagement", "Static — rigid presentation", AMBER)
            pitchRange > PITCH_HIGH || yawRange > PITCH_HIGH ->
                FeatureResult("Head Engagement", "High Movement", AMBER)
            else ->
                FeatureResult("Head Engagement", "Natural", GREEN)
        }

        val overallRes = when {
            varScore < VAR_VERY_LOW -> FeatureResult("Overall Affect",
                "Very Low — blunted affect", RED)
            varScore < VAR_LOW      -> FeatureResult("Overall Affect",
                "Reduced", AMBER)
            else                    -> FeatureResult("Overall Affect",
                "Normal", GREEN)
        }

        val issues = mutableListOf<String>().apply {
            if (blinkRes.color == RED)    add("Critically low blink rate — hypomimia pattern")
            if (blinkRes.color == AMBER && blinkRateBpm < BLINK_LOW)
                                          add("Below-normal blink rate")
            if (blinkRes.color == RED && blinkRateBpm > BLINK_VERY_HIGH)
                                          add("Significantly elevated blink rate")
            if (expressRes.color == RED)  add("Masked affect — very low spontaneous expression")
            if (expressRes.color == AMBER)add("Reduced facial expressiveness")
            if (mouthRes.color == AMBER)  add("Limited speech-related mouth movement")
            if (browRes.color == RED)     add("Flat affect — near-zero brow reactivity")
            if (browRes.color == AMBER)   add("Reduced brow reactivity")
            if (gazeRes.color == RED)     add("Sustained gaze avoidance")
            if (gazeRes.color == AMBER)   add("Reduced gaze engagement")
            if (headRes.color == AMBER && pitchRange < PITCH_STATIC)
                                          add("Rigid head presentation")
            if (overallRes.color == RED)  add("Severely blunted affect across all channels")
            if (overallRes.color == AMBER)add("Reduced overall facial expressivity")
        }

        fun riskValue(result: FeatureResult) = when (result.color) {
            GREEN -> 0.0f
            AMBER -> 0.5f
            else  -> 1.0f   // RED
        }

        val compositeRisk =
            riskValue(blinkRes)   * W_BLINK   +
            riskValue(expressRes) * W_EXPRESS +
            riskValue(mouthRes)   * W_MOUTH   +
            riskValue(browRes)    * W_BROW    +
            riskValue(gazeRes)    * W_GAZE    +
            riskValue(headRes)    * W_HEAD    +
            riskValue(overallRes) * W_OVERALL

        return FaceFeatures(
            blinkRate           = blinkRes,
            expressiveness      = expressRes,
            mouthMovement       = mouthRes,
            reactivity          = browRes,
            engagement          = gazeRes,
            headStability       = headRes,
            overallVariability  = overallRes,
            detectedIssues      = issues,
            compositeRiskScore  = compositeRisk
        )
    }
}
