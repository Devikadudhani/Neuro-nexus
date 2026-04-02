package com.runanywhere.kotlin_starter_example.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.runanywhere.kotlin_starter_example.R

@Composable
fun SignupHealthScreen(navController: NavController) {

    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var blood by remember { mutableStateOf("") }

    var history by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var family by remember { mutableStateOf("") }
    var other by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F1F8))
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Sign up",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )




        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color(0xFFF4F1F8),
                    RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                )
                .padding(horizontal = 24.dp)
        ) {

            Text(
                "Health Information:",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                SmallField("Height", height) { height = it }
                SmallField("Weight (kg)", weight) { weight = it }
                SmallField("Blood", blood) { blood = it }
            }

            Spacer(modifier = Modifier.height(16.dp))

            BigField("Medical History", history) { history = it }
            Spacer(modifier = Modifier.height(14.dp))

            BigField("Allergies", allergies) { allergies = it }
            Spacer(modifier = Modifier.height(14.dp))

            BigField("Family Medical History", family) { family = it }
            Spacer(modifier = Modifier.height(14.dp))

            BigField("Other Important Information", other) { other = it }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate("signup_profile") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5E3A79)
                )
            ) {
                Text(
                    "Next",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun SmallField(
    placeholder: String,
    value: String,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        placeholder = { Text(placeholder) },
        modifier = Modifier
            .height(56.dp),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun BigField(
    placeholder: String,
    value: String,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        placeholder = { Text(placeholder) },
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(18.dp)
    )
}