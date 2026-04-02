package com.runanywhere.kotlin_starter_example.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Transgender
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
fun SignupPersonalScreen(navController: NavController) {

    var name by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F1F8))
            .padding(horizontal = 24.dp)
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
            text = "Sign up",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = "Just a few things, and you’re in!",
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Progress bar
        LinearProgressIndicator(
            progress = 0.25f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = Color(0xFF5E3A79),
            trackColor = Color(0xFFE0E0E0)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Personal Details:",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Name
        Text("Name:", fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Full name") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // DOB
        Text("Date of Birth:", fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = dob,
            onValueChange = { dob = it },
            placeholder = { Text("DD/MM/YYYY") },
            leadingIcon = {
                Icon(Icons.Default.CalendarToday, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Gender:", fontWeight = FontWeight.Medium)

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            GenderBox("Male", Icons.Default.Male, gender == "Male") {
                gender = "Male"
            }

            GenderBox("Female", Icons.Default.Female, gender == "Female") {
                gender = "Female"
            }

            GenderBox("Transgender", Icons.Default.Transgender, gender == "Transgender") {
                gender = "Transgender"
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { navController.navigate("signup_contact") },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5E3A79)
            )
        ) {
            Text(
                text = "Next",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun GenderBox(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(100.dp)
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .border(
                    1.dp,
                    Color(0xFF5E3A79),
                    RoundedCornerShape(16.dp)
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color(0xFF5E3A79),
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(text)
    }
}