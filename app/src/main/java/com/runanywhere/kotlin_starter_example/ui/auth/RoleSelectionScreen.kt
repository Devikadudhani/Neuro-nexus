package com.runanywhere.kotlin_starter_example.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController

@Composable
fun RoleSelectionScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F1F8))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("Sign up", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("Where are you signing up as?")

        Spacer(modifier = Modifier.height(24.dp))

        val roles = listOf("Individual", "Healthcare Professional", "Caregiver")

        roles.forEach { role ->
            Button(
                onClick = { navController.navigate("signup_personal") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Text(role)
            }
        }
    }
}
