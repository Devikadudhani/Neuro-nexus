package com.runanywhere.kotlin_starter_example.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.runanywhere.kotlin_starter_example.tts.TtsController
import com.runanywhere.kotlin_starter_example.ui.components.CustomBottomBar
import com.runanywhere.kotlin_starter_example.ui.components.NeuroTopBar
import kotlinx.coroutines.delay

// Data model
data class RecallQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: String
)

@Composable
fun RecallQuestionScreen(navController: NavController) {

    // ---------- TTS ----------
    val context = LocalContext.current
    val ttsController = remember { TtsController(context) }

    DisposableEffect(Unit) {
        onDispose { ttsController.shutdown() }
    }

    // ---------- Questions ----------
    val questions = listOf(
        RecallQuestion("1. Who found the puppy?",
            listOf("Riya", "Maya", "Sita", "Nita"), "Riya"),

        RecallQuestion("2. Where did Riya find the puppy?",
            listOf("At her school", "Near the park", "At the market", "In her house"),
            "Near the park"),

        RecallQuestion("3. What did Riya give the puppy?",
            listOf("Milk", "Bread", "Biscuits and a warm place", "Water only"),
            "Biscuits and a warm place"),

        RecallQuestion("4. What did Riya do the next day?",
            listOf("Took the puppy to school", "Sold the puppy",
                "Made posters", "Left the puppy in the park"),
            "Made posters"),

        RecallQuestion("5. Who came in the evening?",
            listOf("A doctor", "A teacher",
                "A young boy who was the owner", "A police officer"),
            "A young boy who was the owner")
    )
    @Composable
    fun AnswerOption(
        text: String,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected)
                    Color(0xFFB39DDB)
                else
                    Color(0xFFE0E0E0)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clickable { onClick() }
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(16.dp),
                fontSize = 16.sp,
                color = if (isSelected) Color.White else Color.Black
            )
        }
    }

    // ---------- State ----------
    var currentIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var score by remember { mutableStateOf(0) }

    // ---------- Timer ----------
    var elapsedSeconds by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            elapsedSeconds++
        }
    }

    val currentQuestion = questions[currentIndex]

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

            // Question card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = currentQuestion.question,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Options
            currentQuestion.options.forEach { option ->
                AnswerOption(
                    text = option,
                    isSelected = selectedAnswer == option,
                    onClick = { selectedAnswer = option }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Live timer
            Text(
                text = "Time: ${elapsedSeconds}s",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Next button
            Button(
                onClick = {
                    if (selectedAnswer == currentQuestion.correctAnswer) {
                        score++
                    }

                    if (currentIndex < questions.lastIndex) {
                        currentIndex++
                        selectedAnswer = null
                    } else {
                        // Navigate with score + time
                        navController.navigate("recall_result/$score/$elapsedSeconds")
                    }
                },
                enabled = selectedAnswer != null,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB39DDB)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = if (currentIndex == questions.lastIndex) "FINISH" else "NEXT",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Read aloud button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FloatingActionButton(
                    onClick = {
                        val textToRead = buildString {
                            append(currentQuestion.question)
                            currentQuestion.options.forEach {
                                append(". $it")
                            }
                        }
                        ttsController.speak(textToRead)
                    },
                    containerColor = Color(0xFF8E6BAF)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Read aloud",
                        tint = Color.White
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
}
