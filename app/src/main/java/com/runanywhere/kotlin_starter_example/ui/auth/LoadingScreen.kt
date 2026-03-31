package com.runanywhere.kotlin_starter_example.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(navController: NavController) {

    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate("dashboard") {
            popUpTo("welcome") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F1F8)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
