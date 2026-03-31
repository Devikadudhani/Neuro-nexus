package com.runanywhere.kotlin_starter_example.face

import android.graphics.Bitmap
import android.util.Log
import com.runanywhere.kotlin_starter_example.model.FaceLandmarks
import com.runanywhere.kotlin_starter_example.model.Point

class CalibrationManager {
    var earBaseline: Float = 0.25f
    var marBaseline: Float = 0.3f
    var brightnessBaseline: Int = 120
    var isCalibrated = false
    
    private val earHistory = mutableListOf<Float>()
    private val marHistory = mutableListOf<Float>()
    private val brightnessHistory = mutableListOf<Int>()
    
    private val calibrationDurationMs = 4000L
    private var startTime = 0L

    fun startCalibration() {
        startTime = System.currentTimeMillis()
        earHistory.clear()
        marHistory.clear()
        brightnessHistory.clear()
        isCalibrated = false
    }

    fun update(landmarks: FaceLandmarks, bitmap: Bitmap): Boolean {
        if (isCalibrated) return true
        
        val ear = (FaceFeatureExtractor.calculateEAR(landmarks.leftEye) + 
                  FaceFeatureExtractor.calculateEAR(landmarks.rightEye)) / 2f
        val mar = FaceFeatureExtractor.calculateMAR(landmarks.lips)
        val brightness = calculateBrightness(bitmap)
        
        earHistory.add(ear)
        marHistory.add(mar)
        brightnessHistory.add(brightness)
        
        if (System.currentTimeMillis() - startTime >= calibrationDurationMs) {
            earBaseline = earHistory.average().toFloat()
            marBaseline = marHistory.average().toFloat()
            brightnessBaseline = brightnessHistory.average().toInt()
            isCalibrated = true
            Log.d("Calibration", "Calibrated: EAR=$earBaseline, MAR=$marBaseline, Brightness=$brightnessBaseline")
        }
        return isCalibrated
    }

    private fun calculateBrightness(bitmap: Bitmap): Int {
        var totalIntensity = 0L
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        
        for (pixel in pixels) {
            val r = (pixel shr 16) and 0xff
            val g = (pixel shr 8) and 0xff
            val b = pixel and 0xff
            totalIntensity += (0.299 * r + 0.587 * g + 0.114 * b).toInt()
        }
        return (totalIntensity / (width * height)).toInt()
    }
    
    fun getLightingStatus(): String {
        return when {
            brightnessBaseline < 80 -> "Low Light"
            brightnessBaseline > 180 -> "Too Bright"
            else -> "Normal"
        }
    }
}
