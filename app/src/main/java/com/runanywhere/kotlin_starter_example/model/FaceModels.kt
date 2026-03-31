package com.runanywhere.kotlin_starter_example.model

data class FaceLandmarks(
    val leftEye: List<Point>,
    val rightEye: List<Point>,
    val lips: List<Point>,
    val noseTip: Point,
    val faceBox: Rect? = null
)

data class Point(val x: Float, val y: Float, val z: Float = 0f)
data class Rect(val left: Float, val top: Float, val right: Float, val bottom: Float)

data class FaceFeatures(
    val blinkRateBpm: Float,
    val blinkCategory: String, // "Low", "Normal", "High"
    val headStabilityScore: Int, // 0-100
    val headCategory: String, // "Stable", "Slightly Unstable", "Unstable"
    val reactionTimeMs: Long?,
    val reactionCategory: String, // "Fast", "Normal", "Slow", "N/A"
    val facialActivity: String, // "Low", "Normal"
    val riskScore: Int,
    val riskLevel: String,
    val detectedIssues: List<String>
)
