package com.runanywhere.kotlin_starter_example.face

import com.runanywhere.kotlin_starter_example.model.Point
import kotlin.math.pow
import kotlin.math.sqrt

object FaceFeatureExtractor {

    private const val MOVING_AVERAGE_WINDOW = 5

    fun calculateEAR(eyePoints: List<Point>): Float {
        if (eyePoints.size < 6) return 0f
        val v1 = distance(eyePoints[1], eyePoints[5])
        val v2 = distance(eyePoints[2], eyePoints[4])
        val h = distance(eyePoints[0], eyePoints[3])
        return (v1 + v2) / (2f * h)
    }

    fun calculateMAR(lipsPoints: List<Point>): Float {
        if (lipsPoints.size < 4) return 0f
        // Vertical distance: top and bottom lip center
        val v = distance(lipsPoints[2], lipsPoints[3])
        // Horizontal distance: left and right lip corners
        val h = distance(lipsPoints[0], lipsPoints[1])
        return if (h > 0) v / h else 0f
    }

    fun distance(p1: Point, p2: Point): Float {
        return sqrt((p1.x - p2.x).pow(2) + (p1.y - p2.y).pow(2))
    }

    class Smoother(private val windowSize: Int = MOVING_AVERAGE_WINDOW) {
        private val history = mutableListOf<Float>()

        fun addAndGet(value: Float): Float {
            history.add(value)
            if (history.size > windowSize) history.removeAt(0)
            return history.average().toFloat()
        }
    }

    fun calculateRollingVariance(data: List<Float>): Float {
        if (data.isEmpty()) return 0f
        val mean = data.average().toFloat()
        return data.map { (it - mean).pow(2) }.average().toFloat()
    }
}
