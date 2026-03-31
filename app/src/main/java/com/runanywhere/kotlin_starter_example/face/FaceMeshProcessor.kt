package com.runanywhere.kotlin_starter_example.face

import android.content.Context
import android.graphics.Bitmap
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import com.runanywhere.kotlin_starter_example.model.Point
import com.runanywhere.kotlin_starter_example.model.FaceLandmarks
import com.runanywhere.kotlin_starter_example.model.Rect

class FaceMeshProcessor(context: Context) {
    private var faceLandmarker: FaceLandmarker? = null

    init {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("face_landmarker.task")
            .build()

        val options = FaceLandmarker.FaceLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setMinFaceDetectionConfidence(0.5f)
            .setMinTrackingConfidence(0.5f)
            .setMinFacePresenceConfidence(0.5f)
            .setRunningMode(RunningMode.IMAGE)
            .build()

        try {
            faceLandmarker = FaceLandmarker.createFromOptions(context, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun processFrame(bitmap: Bitmap): FaceLandmarks? {
        val mpImage = BitmapImageBuilder(bitmap).build()
        val result: FaceLandmarkerResult? = faceLandmarker?.detect(mpImage)

        // Extract landmarks for the first detected face
        val landmarks = result?.faceLandmarks()?.firstOrNull() ?: return null
        
        // Manual bounding box calculation for normalization
        var minX = 1.0f
        var minY = 1.0f
        var maxX = 0.0f
        var maxY = 0.0f
        
        for (landmark in landmarks) {
            val x = landmark.x()
            val y = landmark.y()
            if (x < minX) minX = x
            if (y < minY) minY = y
            if (x > maxX) maxX = x
            if (y > maxY) maxY = y
        }
        
        val faceBox = Rect(minX, minY, maxX, maxY)

        return FaceLandmarks(
            leftEye = listOf(362, 385, 387, 263, 373, 380).map { i -> 
                val l = landmarks[i]
                Point(l.x(), l.y(), l.z()) 
            },
            rightEye = listOf(33, 160, 158, 133, 153, 144).map { i -> 
                val l = landmarks[i]
                Point(l.x(), l.y(), l.z()) 
            },
            lips = listOf(61, 291, 0, 17).map { i -> 
                val l = landmarks[i]
                Point(l.x(), l.y(), l.z()) 
            },
            noseTip = landmarks[1].let { l -> Point(l.x(), l.y(), l.z()) },
            faceBox = faceBox
        )
    }
}
