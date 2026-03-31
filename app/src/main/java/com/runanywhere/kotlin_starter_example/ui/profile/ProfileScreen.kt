package com.runanywhere.kotlin_starter_example.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.runanywhere.kotlin_starter_example.R
import com.runanywhere.kotlin_starter_example.ui.components.CustomBottomBar
import com.runanywhere.kotlin_starter_example.ui.components.NeuroTopBar

@Composable
fun ProfileScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F1F8))
    ) {

        NeuroTopBar(navController)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            // Profile Image
            Image(
                painter = painterResource(R.drawable.img),
                contentDescription = "Profile",
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Harshita Bansal",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF6B4E8E)
            )

            Text(
                text = "harshita006",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    StatItem(
                        icon = Icons.Default.Description,
                        value = "8/10",
                        label = ""
                    )

                    StatItem(
                        icon = Icons.Default.Analytics,
                        value = "",
                        label = "Analytics"
                    )

                    StatItem(
                        icon = Icons.Default.CalendarToday,
                        value = "8",
                        label = "Last Task"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Menu Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {

                    ProfileMenuItem("Edit Profile") { }
                    Divider()
                    ProfileMenuItem("Change Password") { }
                    Divider()
                    ProfileMenuItem("Saved Articles") { }
                    Divider()
                    ProfileMenuItem("My Appointments") { }
                    Divider()
                    ProfileMenuItem("Settings") { }
                }
            }
        }

        CustomBottomBar(
            onHomeClick = { navController.navigate("dashboard") },
            onTasksClick = { navController.navigate("tasks") },
            onSettingsClick = { navController.navigate("profile") },
            onShareClick = { navController.navigate("community") }
        )
    }
}

@Composable
fun StatItem(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = Color(0xFF6B4E8E))
        if (value.isNotEmpty()) {
            Text(value, fontWeight = FontWeight.Bold)
        }
        if (label.isNotEmpty()) {
            Text(label, fontSize = 12.sp)
        }
    }
}

@Composable
fun ProfileMenuItem(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}
