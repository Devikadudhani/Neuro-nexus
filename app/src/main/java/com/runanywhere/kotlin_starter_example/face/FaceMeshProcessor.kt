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
import kotlin.math.atan2
import kotlin.math.sqrt

class FaceMeshProcessor(context: Context) {
    private var faceLandmarker: FaceLandmarker? = null

    // ─── Landmark Index Constants ───────────────────────────────────────────────

    // Left Eye (from camera's perspective = user's left)
    // Outer corner, inner corner, top-1, top-2, bottom-1, bottom-2
    private val LEFT_EYE_OUTER  = 263
    private val LEFT_EYE_INNER  = 362
    private val LEFT_EYE_TOP1   = 387   // was 385 — corrected
    private val LEFT_EYE_TOP2   = 386
    private val LEFT_EYE_BOT1   = 373
    private val LEFT_EYE_BOT2   = 374

    // Right Eye
    private val RIGHT_EYE_OUTER = 33
    private val RIGHT_EYE_INNER = 133
    private val RIGHT_EYE_TOP1  = 160   // was 159 — corrected
    private val RIGHT_EYE_TOP2  = 158
    private val RIGHT_EYE_BOT1  = 144   // was 145 — corrected
    private val RIGHT_EYE_BOT2  = 153

    // Eyebrows — top-center points
    private val LEFT_BROW_CENTER  = 295
    private val RIGHT_BROW_CENTER = 65
    // Upper eyelid reference for brow height
    private val LEFT_UPPER_LID   = 386
    private val RIGHT_UPPER_LID  = 158

    // Lips
    private val LIP_LEFT   = 61
    private val LIP_RIGHT  = 291
    private val LIP_TOP    = 13
    private val LIP_BOTTOM = 14

    // Head pose references
    private val NOSE_TIP = 1
    private val CHIN     = 152

    // Iris (only present if model outputs 478 landmarks)
    private val LEFT_IRIS_START  = 468
    private val RIGHT_IRIS_START = 473

    init {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("face_landmarker.task")
            .build()

        val options = FaceLandmarker.FaceLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setMinFaceDetectionConfidence(0.5f)
            .setMinTrackingConfidence(0.5f)
            .setMinFacePresenceConfidence(0.5f)
            // Use IMAGE for single-frame / on-demand processing.
            // Switch to LIVE_STREAM + listener if calling from CameraX frame callback.
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
        val result: FaceLandmarkerResult = faceLandmarker?.detect(mpImage) ?: return null
        val landmarks = result.faceLandmarks().firstOrNull() ?: return null

        val width  = bitmap.width.toFloat()
        val height = bitmap.height.toFloat()
        val aspectRatio = width / height  // used to correct x distances in normalized space

        // ── Bounding box ──────────────────────────────────────────────────────
        var minX = 1.0f; var minY = 1.0f
        var maxX = 0.0f; var maxY = 0.0f
        for (lm in landmarks) {
            if (lm.x() < minX) minX = lm.x()
            if (lm.y() < minY) minY = lm.y()
            if (lm.x() > maxX) maxX = lm.x()
            if (lm.y() > maxY) maxY = lm.y()
        }
        val faceBox   = Rect(minX, minY, maxX, maxY)
        val faceWidth  = maxX - minX
        val faceHeight = maxY - minY   // needed for brow normalization

        // ── 1. EAR (Eye Aspect Ratio) ─────────────────────────────────────────
        // Formula: EAR = (|p2-p6| + |p3-p5|) / (2 * |p1-p4|)
        // where p1/p4 = horizontal corners, p2/p3/p5/p6 = vertical pairs

        val leftEar = eyeAspectRatio(
            top1   = landmarks[LEFT_EYE_TOP1],
            top2   = landmarks[LEFT_EYE_TOP2],
            bot1   = landmarks[LEFT_EYE_BOT1],
            bot2   = landmarks[LEFT_EYE_BOT2],
            inner  = landmarks[LEFT_EYE_INNER],
            outer  = landmarks[LEFT_EYE_OUTER],
            ar     = aspectRatio
        )
        val rightEar = eyeAspectRatio(
            top1   = landmarks[RIGHT_EYE_TOP1],
            top2   = landmarks[RIGHT_EYE_TOP2],
            bot1   = landmarks[RIGHT_EYE_BOT1],
            bot2   = landmarks[RIGHT_EYE_BOT2],
            inner  = landmarks[RIGHT_EYE_INNER],
            outer  = landmarks[RIGHT_EYE_OUTER],
            ar     = aspectRatio
        )
        val avgEar = (leftEar + rightEar) / 2.0f

        // ── 2. Smile Score ─────────────────────────────────────────────────────
        // Mouth width normalized by face width — no double aspect ratio correction
        val lipLeft  = landmarks[LIP_LEFT]
        val lipRight = landmarks[LIP_RIGHT]
        val smileScore = dist(lipLeft, lipRight, aspectRatio) / faceWidth
        // Note: faceWidth is already in normalized coords; dist() corrects x by aspectRatio.
        // Both sides of the division are now in the same corrected space.

        // ── 3. MAR (Mouth Aspect Ratio / Openness) ────────────────────────────
        val mouthTop    = landmarks[LIP_TOP]
        val mouthBottom = landmarks[LIP_BOTTOM]
        val mouthOpenness = dist(mouthTop, mouthBottom, aspectRatio) /
                dist(lipLeft, lipRight, aspectRatio)

        // ── 4. Brow Height ────────────────────────────────────────────────────
        // Distance from brow center down to upper eyelid, normalized by face height
        val leftBrowHeight  = dist(landmarks[LEFT_BROW_CENTER],  landmarks[LEFT_UPPER_LID],  aspectRatio) / faceHeight
        val rightBrowHeight = dist(landmarks[RIGHT_BROW_CENTER], landmarks[RIGHT_UPPER_LID], aspectRatio) / faceHeight
        val avgBrowHeight   = (leftBrowHeight + rightBrowHeight) / 2.0f

        // ── 5. Head Pose ──────────────────────────────────────────────────────
        val nose  = landmarks[NOSE_TIP]
        val chin  = landmarks[CHIN]
        val lEyeI = landmarks[LEFT_EYE_INNER]
        val rEyeI = landmarks[RIGHT_EYE_INNER]

        // Yaw: horizontal offset of nose tip from midpoint between eye inner corners
        val eyeMidX = (lEyeI.x() + rEyeI.x()) / 2.0f
        val yaw = (nose.x() - eyeMidX) / faceWidth

        // Pitch: vertical position of nose relative to chin-to-eye-mid axis
        // Positive = looking down, negative = looking up
        val eyeMidY   = (lEyeI.y() + rEyeI.y()) / 2.0f
        val faceVert  = chin.y() - eyeMidY  // total vertical face span (normalized)
        val pitch = if (faceVert != 0f)
            (nose.y() - eyeMidY) / faceVert - 0.5f   // centered around 0
        else 0f

        // Roll: angle of the eye-to-eye axis from horizontal
        val roll = atan2(
            (rEyeI.y() - lEyeI.y()).toDouble(),
            (rEyeI.x() - lEyeI.x()).toDouble()
        ).toFloat()

        // ── Build result ──────────────────────────────────────────────────────
        return FaceLandmarks(
            leftEye = listOf(
                LEFT_EYE_INNER, LEFT_EYE_OUTER,
                LEFT_EYE_TOP1, LEFT_EYE_TOP2,
                LEFT_EYE_BOT1, LEFT_EYE_BOT2
            ).map { i -> landmarks[i].let { Point(it.x(), it.y(), it.z()) } },

            rightEye = listOf(
                RIGHT_EYE_INNER, RIGHT_EYE_OUTER,
                RIGHT_EYE_TOP1, RIGHT_EYE_TOP2,
                RIGHT_EYE_BOT1, RIGHT_EYE_BOT2
            ).map { i -> landmarks[i].let { Point(it.x(), it.y(), it.z()) } },

            leftEyebrow = listOf(276, 283, 282, 295, 285)
                .map { i -> landmarks[i].let { Point(it.x(), it.y(), it.z()) } },

            rightEyebrow = listOf(46, 53, 52, 65, 55)
                .map { i -> landmarks[i].let { Point(it.x(), it.y(), it.z()) } },

            lips = listOf(LIP_LEFT, LIP_RIGHT, 0, 17, LIP_TOP, LIP_BOTTOM)
                .map { i -> landmarks[i].let { Point(it.x(), it.y(), it.z()) } },

            leftIris = if (landmarks.size > LEFT_IRIS_START)
                (LEFT_IRIS_START until LEFT_IRIS_START + 5)
                    .filter { it < landmarks.size }
                    .map { i -> landmarks[i].let { Point(it.x(), it.y(), it.z()) } }
            else emptyList(),

            rightIris = if (landmarks.size > RIGHT_IRIS_START)
                (RIGHT_IRIS_START until RIGHT_IRIS_START + 5)
                    .filter { it < landmarks.size }
                    .map { i -> landmarks[i].let { Point(it.x(), it.y(), it.z()) } }
            else emptyList(),

            noseTip       = landmarks[NOSE_TIP].let { Point(it.x(), it.y(), it.z()) },
            faceBox       = faceBox,
            ear           = avgEar,
            smileScore    = smileScore,
            mouthOpenness = mouthOpenness,
            browHeight    = avgBrowHeight,
            pitch         = pitch,
            yaw           = yaw,
            roll          = roll
        )
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun eyeAspectRatio(
        top1: com.google.mediapipe.tasks.components.containers.NormalizedLandmark,
        top2: com.google.mediapipe.tasks.components.containers.NormalizedLandmark,
        bot1: com.google.mediapipe.tasks.components.containers.NormalizedLandmark,
        bot2: com.google.mediapipe.tasks.components.containers.NormalizedLandmark,
        inner: com.google.mediapipe.tasks.components.containers.NormalizedLandmark,
        outer: com.google.mediapipe.tasks.components.containers.NormalizedLandmark,
        ar: Float
    ): Float {
        val vertical   = dist(top1, bot1, ar) + dist(top2, bot2, ar)
        val horizontal = dist(inner, outer, ar)
        return if (horizontal > 0f) vertical / (2.0f * horizontal) else 0f
    }

    private fun dist(
        p1: com.google.mediapipe.tasks.components.containers.NormalizedLandmark,
        p2: com.google.mediapipe.tasks.components.containers.NormalizedLandmark,
        aspectRatio: Float
    ): Float {
        val dx = (p1.x() - p2.x()) * aspectRatio
        val dy = p1.y() - p2.y()
        return sqrt(dx * dx + dy * dy)
    }
}