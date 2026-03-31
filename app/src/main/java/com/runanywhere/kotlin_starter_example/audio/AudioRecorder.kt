package com.runanywhere.kotlin_starter_example.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class AudioRecorder(
    private val context: Context,
    private val sampleRate: Int = 16000
) {

    suspend fun record(seconds: Int): ShortArray = withContext(Dispatchers.IO) {

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw SecurityException("RECORD_AUDIO permission not granted")
        }

        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        val totalSamples = sampleRate * seconds
        val data = ShortArray(totalSamples)

        recorder.startRecording()
        recorder.read(data, 0, totalSamples)
        recorder.stop()
        recorder.release()

        data
    }

     suspend fun recordToByteArray(durationSec: Int): ByteArray {
        val samples = record(durationSec)

        val byteBuffer = ByteArray(samples.size * 2)
        var i = 0

        for (sample in samples) {
            byteBuffer[i++] = (sample.toInt() and 0x00FF).toByte()
            byteBuffer[i++] = ((sample.toInt() shr 8) and 0x00FF).toByte()
        }

        return byteBuffer
    }

//    fun recordToFile(durationSec: Int): File {
//        val file = File(context.cacheDir, "audio.wav")
//
//        // record audio and save into file
//        // (reuse your existing logic but write into file)
//
//        return file
//    }
}
