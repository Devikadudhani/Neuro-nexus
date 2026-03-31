package com.runanywhere.kotlin_starter_example.ui.tasks

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.runanywhere.kotlin_starter_example.R
import com.runanywhere.kotlin_starter_example.ui.components.CustomBottomBar
import com.runanywhere.kotlin_starter_example.ui.components.NeuroTopBar
import com.runanywhere.kotlin_starter_example.ui.components.SpeakerFab
@Composable
fun StroopResultScreen(
    navController: NavController,
    score: Int,
    timeSeconds: Int
) {
    val totalQuestions = 5
    val errors = totalQuestions - score

    // Format time into mm:ss
    val minutes = timeSeconds / 60
    val seconds = timeSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    // Text for read aloud
    val resultText =
        "You scored $score out of $totalQuestions. You made $errors errors. " +
                "Total time taken was $formattedTime."

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F1F8))
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            NeuroTopBar()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Great!\nYou have completed this task",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4A4A4A)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Time circle (same style as recall score circle)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(140.dp)
                        .background(Color(0xFFB39DDB), shape = CircleShape)
                ) {
                    Text(
                        text = "Time\n$formattedTime",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Score: $score/$totalQuestions",
                    color = Color(0xFF6B4E8E),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "✕ $errors Errors",
                    color = Color(0xFFD32F2F),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Mascot
                Image(
                    painter = painterResource(id = R.drawable.mascot_img),
                    contentDescription = "Mascot",
                    modifier = Modifier.size(220.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Keep practising to improve your performance",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        navController.navigate("tasks")
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB39DDB)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "NEXT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    FloatingActionButton(
                        onClick = { },
                        containerColor = Color(0xFF8E6BAF)
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
                onSettingsClick = {navController.navigate("settings") },
                onShareClick = { navController.navigate("community") }
            )
        }

        SpeakerFab(
            textToRead = resultText,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 90.dp)
        )
    }
}
