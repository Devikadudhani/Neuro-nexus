package com.runanywhere.kotlin_starter_example.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SignupInsuranceScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F1F8))
            .padding(24.dp)
    ) {

        Text("Insurance Information", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate("signup_profile") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Next")
        }
    }
}
