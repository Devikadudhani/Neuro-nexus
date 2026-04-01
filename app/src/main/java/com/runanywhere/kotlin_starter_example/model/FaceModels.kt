package com.runanywhere.kotlin_starter_example.model

import kotlin.math.sqrt
import kotlin.math.pow

data class FaceLandmarks(
    val leftEye: List<Point>,
    val rightEye: List<Point>,
    val leftEyebrow: List<Point>,
    val rightEyebrow: List<Point>,
    val lips: List<Point>,
    val leftIris: List<Point>,
    val rightIris: List<Point>,
    val noseTip: Point,
    val faceBox: Rect? = null,
    
    // Per-frame calculated metrics
    val ear: Float = 0f,
    val smileScore: Float = 0f,
    val mouthOpenness: Float = 0f,
    val browHeight: Float = 0f,
    val pitch: Float = 0f,
    val yaw: Float = 0f,
    val roll: Float = 0f
)

data class Point(val x: Float, val y: Float, val z: Float = 0f) {
    fun distanceTo(other: Point): Float {
        return sqrt(
            (x - other.x).toDouble().pow(2.0) +
            (y - other.y).toDouble().pow(2.0) +
            (z - other.z).toDouble().pow(2.0)
        ).toFloat()
    }
}

data class Rect(val left: Float, val top: Float, val right: Float, val bottom: Float)

data class FeatureResult(
    val label: String,
    val value: String,
    val color: Long 
)

data class FaceFeatures(
    val blinkRate: FeatureResult,
    val expressiveness: FeatureResult,
    val mouthMovement: FeatureResult,
    val reactivity: FeatureResult,
    val engagement: FeatureResult,
    val headStability: FeatureResult,
    val overallVariability: FeatureResult,
    val detectedIssues: List<String>,
    val compositeRiskScore: Float = 0f,
    val aiInsight: String? = null // New field for LLM analysis
)
