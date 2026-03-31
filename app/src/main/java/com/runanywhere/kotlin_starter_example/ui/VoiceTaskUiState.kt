package com.runanywhere.kotlin_starter_example.ui

import com.runanywhere.kotlin_starter_example.model.VoiceTaskResult

data class VoiceTaskUiState(
    val isRecording: Boolean = false,
    val isCompleted: Boolean = false,
    val status: String = "Tap start and speak...",
    val transcript: String = "",
    val result: VoiceTaskResult? = null,
    val timerSeconds: Int = 20
)