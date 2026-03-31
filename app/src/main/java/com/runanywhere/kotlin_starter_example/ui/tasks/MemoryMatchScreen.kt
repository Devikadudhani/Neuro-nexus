package com.runanywhere.kotlin_starter_example.ui.tasks

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun MemoryMatchScreen(navController: NavController) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val readAloudText =
        "Memory Match. You will be shown pictures with names. Try to remember each pair. Later you will be asked to match the correct name with its picture."

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F1F8))
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // 🔹 Your existing top bar
            NeuroTopBar()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "MEMORY MATCH",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B4E8E)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Text(
                        text = "You will be shown pictures with names. Try to remember each pair. Later you’ll be asked to match the correct name with its picture.",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
                @Suppress("UnusedBoxWithConstraintsScope")
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                ) {

                    val boxWidth = maxWidth
                    val boxHeight = maxHeight

                    // 🔹 Mascot Image
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

                    // 🔹 Start Button
                    Button(
                        onClick = { navController.navigate("memory_preview") },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB39DDB)
                        ),
                        modifier = Modifier
                            .height(55.dp)
                            .width(170.dp)
                            .align(Alignment.CenterEnd)
                            .offset(
                                y = boxHeight * (-0.32f)
                            )
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

            CustomBottomBar(
                onHomeClick = { navController.navigate("dashboard") },
                onTasksClick = { navController.navigate("tasks") },
                onSettingsClick = { },
                onShareClick = { navController.navigate("community") }
            )
        }

        // 🔊 Read-aloud FAB (added only this)
        SpeakerFab(
            textToRead = readAloudText,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp)
        )
    }
}
