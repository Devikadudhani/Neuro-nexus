package com.runanywhere.kotlin_starter_example.ui.tasks

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.runanywhere.kotlin_starter_example.R
import com.runanywhere.kotlin_starter_example.ui.components.CustomBottomBar
import com.runanywhere.kotlin_starter_example.ui.components.NeuroTopBar
import com.runanywhere.kotlin_starter_example.ui.components.SpeakerFab

@Composable
fun StroopIntroScreen(navController: NavController) {

    val screenText =
        "Focus on the word shown on the screen. Your task is to select the color of the letters, not the word itself. Respond as quickly and accurately as you can."

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F1F8))
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            // Top bar
            NeuroTopBar()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "STROOP TEST",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B4E8E)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = screenText,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                @Suppress("UnusedBoxWithConstraintsScope")
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val boxWidth = maxWidth
                    val boxHeight = maxHeight

                    // Mascot
                    Image(
                        painter = painterResource(id = R.drawable.mascot_img),
                        contentDescription = "Mascot",
                        modifier = Modifier
                            .size(boxWidth * 1.3f)
                            .align(Alignment.BottomStart)
                            .offset(
                                x = boxWidth * (-0.09f),
                                y = boxHeight * (-0.05f)
                            ),
                        contentScale = ContentScale.Crop
                    )

                    // Start button
                    Button(
                        onClick = {
                            navController.navigate("stroop_game")
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB39DDB)
                        ),
                        modifier = Modifier
                            .height(55.dp)
                            .width(170.dp)
                            .align(Alignment.CenterEnd)
                            .offset(y = boxHeight * (-0.32f))
                            .padding(end = 32.dp)
                    ) {
                        Text(
                            text = "START",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            // Bottom bar
            CustomBottomBar(
                navController = navController,
                onHomeClick = { navController.navigate("dashboard") },
                onTasksClick = { navController.navigate("reports") },
                onSettingsClick = { navController.navigate("settings")},
                onShareClick = { navController.navigate("community") }
            )
        }

        // 🔊 Read-aloud FAB
        SpeakerFab(
            textToRead = screenText,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp)
        )
    }
}
