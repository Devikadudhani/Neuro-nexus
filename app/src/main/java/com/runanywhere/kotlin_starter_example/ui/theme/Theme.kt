package com.runanywhere.kotlin_starter_example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = AccentPurple,
    onPrimary = Color.White,
    background = LavenderShell,
    onBackground = Ink,
    surface = SoftSurface,
    onSurface = Ink,
    error = ErrorRed
)

@Composable
fun NeuroNexusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = MaterialTheme.typography,
        content = content
    )
}
