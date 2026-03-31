package com.runanywhere.kotlin_starter_example.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.runanywhere.kotlin_starter_example.ui.components.CustomBottomBar
import com.runanywhere.kotlin_starter_example.ui.components.NeuroTopBar
import com.runanywhere.kotlin_starter_example.ui.components.SpeakerFab

// Data model

@Composable

fun StroopGameScreen(navController: NavController) {
    var elapsedSeconds by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            elapsedSeconds++
        }
    }

    val questions = listOf(
        StroopQuestion(
            word = "Blue",
            textColor = Color(0xFFFF69B4), // Pink text
            backgroundColor = Color(0xFFFFFF00), // Yellow background
            correctAnswer = "pink"
        ),
        StroopQuestion(
            word = "Red",
            textColor = Color(0xFF43A047), // Green text
            backgroundColor = Color(0xFF8E6BAF), // Purple background
            correctAnswer = "green"
        ),
        StroopQuestion(
            word = "Yellow",
            textColor = Color(0xFF2196F3), // Blue text
            backgroundColor = Color(0xFFFF9800), // Orange background
            correctAnswer = "blue"
        ),
        StroopQuestion(
            word = "Green",
            textColor = Color(0xFFE53935), // Red text
            backgroundColor = Color(0xFFFF69B4), // Pink background
            correctAnswer = "red"
        ),
        StroopQuestion(
            word = "Purple",
            textColor = Color(0xFFFF9800), // Orange text
            backgroundColor = Color(0xFF2196F3), // Blue background
            correctAnswer = "orange"
        )
    )

    var currentIndex by remember { mutableStateOf(0) }
    var answer by remember { mutableStateOf("") }
    var score by remember { mutableStateOf(0) }

    val current = questions[currentIndex]

    val screenText = "Spell the color of the letters shown on the screen."

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

                // Instruction text
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Spell the color of the letters shown on the screen as quickly and accurately as possible.",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Stroop circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(200.dp)
                        .background(current.backgroundColor, shape = CircleShape)
                ) {
                    Text(
                        text = current.word,
                        color = current.textColor,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Answer input
                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    placeholder = { Text("Type your answer") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = String.format("00:%02d", elapsedSeconds),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )


                Spacer(modifier = Modifier.height(30.dp))

                // Next / Finish button
                Button(
                    onClick = {
                        if (answer.trim().lowercase() == current.correctAnswer) {
                            score++
                        }

                        if (currentIndex < questions.lastIndex) {
                            currentIndex++
                            answer = ""
                        } else {
                            val totalTime = elapsedSeconds


                            navController.navigate("stroop_result/$score/$totalTime")
                        }
                    },
                    enabled = answer.isNotBlank(),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9B7BB8)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = if (currentIndex == questions.lastIndex) "FINISH" else "NEXT",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }

            CustomBottomBar(
                navController = navController,
                onHomeClick = { navController.navigate("dashboard") },
                onTasksClick = { navController.navigate("reports") },
                onSettingsClick = { navController.navigate("settings")},
                onShareClick = { navController.navigate("community") }
            )
        }

        // Read aloud button
        SpeakerFab(
            textToRead = screenText,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp)
        )
    }
}
