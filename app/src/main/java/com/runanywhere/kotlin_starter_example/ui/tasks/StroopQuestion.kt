package com.runanywhere.kotlin_starter_example.ui.tasks

import androidx.compose.ui.graphics.Color

data class StroopQuestion(
    val word: String,
    val textColor: Color,
    val backgroundColor: Color,
    val correctAnswer: String
)

