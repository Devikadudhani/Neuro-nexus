package com.runanywhere.kotlin_starter_example.ui.tasks

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.runanywhere.kotlin_starter_example.R
import com.runanywhere.kotlin_starter_example.tts.TtsController
import com.runanywhere.kotlin_starter_example.ui.components.CustomBottomBar
import com.runanywhere.kotlin_starter_example.ui.components.NeuroTopBar

@Composable
fun RecallPhaseScreen(navController: NavController) {

    val screenText =
        "Now you will be asked some questions about what you remember. Answer each question by choosing the correct option. Press Next to continue."

    // 🔊 TTS setup
    val context = LocalContext.current
    val ttsController = remember { TtsController(context) }

    DisposableEffect(Unit) {
        onDispose {
            ttsController.shutdown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F1F8))
    ) {

        NeuroTopBar()

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "NARRATIVE RECALL",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B4E8E)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFFB39DDB)
            ) {
                Text(
                    text = "RECALL PHASE",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    color = Color(0xFF4A2E6E),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Now you will be asked some questions about what you remember. Answer each question aloud by speaking. Press Next to continue.",
                    modifier = Modifier.padding(20.dp),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
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

                // Next button
                Button(
                    onClick = {
                        navController.navigate("recall_question")
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
                        text = "NEXT",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }

                // 🔊 Sound FAB now reads instructions
                FloatingActionButton(
                    onClick = { ttsController.speak(screenText) },
                    containerColor = Color(0xFF8E6BAF),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Sound",
                        tint = Color.White
                    )
                }
            }
        }

        CustomBottomBar(
            navController = navController,
            onHomeClick = { navController.navigate("dashboard") },
            onTasksClick = { navController.navigate("tasks") },
            onSettingsClick = { navController.navigate("settings")},
            onShareClick = { navController.navigate("community") }
        )
    }
}
