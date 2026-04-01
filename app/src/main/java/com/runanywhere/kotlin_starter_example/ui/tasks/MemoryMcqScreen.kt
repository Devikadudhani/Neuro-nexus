package com.runanywhere.kotlin_starter_example.ui.tasks


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.runanywhere.kotlin_starter_example.R
import com.runanywhere.kotlin_starter_example.ui.components.NeuroTopBar
import android.speech.tts.TextToSpeech
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import androidx.compose.runtime.DisposableEffect
import com.runanywhere.kotlin_starter_example.ui.components.SpeakerFab
@Composable
fun MemoryMcqScreen(navController: NavController) {

    val context = LocalContext.current
    var textToSpeech by remember { mutableStateOf<TextToSpeech?>(null) }

    val questionList = remember {
        listOf(
            RecallQuestionMem(
                R.drawable.person_ross,
                "Ross",
                listOf("Mike", "Ross", "Alen", "Max").shuffled()
            ),
            RecallQuestionMem(
                R.drawable.person_rachel,
                "Rachel",
                listOf("Rachel", "Monica", "Lily", "Emma").shuffled()
            ),
            RecallQuestionMem(
                R.drawable.person_monica,
                "Monica",
                listOf("Phoebe", "Monica", "Nina", "Sara").shuffled()
            ),
            RecallQuestionMem(
                R.drawable.person_chandler,
                "Chandler",
                listOf("Ross", "Chandler", "Mike", "David").shuffled()
            )
        )
    }

    var currentIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showAnswer by remember { mutableStateOf(false) }

    val currentQuestion = questionList[currentIndex]

    // TTS init
    LaunchedEffect(Unit) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.US
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
    }

    val readText =
        "Select the correct name for the person shown in the picture."

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F1F8))
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            NeuroTopBar(navController)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                Image(
                    painter = painterResource(currentQuestion.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(20.dp))

                LinearProgressIndicator(
                    progress = (currentIndex + 1) / questionList.size.toFloat(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text("${currentIndex + 1} of ${questionList.size}")

                Spacer(modifier = Modifier.height(20.dp))

                currentQuestion.options.forEach { option ->

                    val backgroundColor = when {
                        showAnswer && option == currentQuestion.correctAnswer -> Color(0xFF81C784)
                        showAnswer && option == selectedAnswer && option != currentQuestion.correctAnswer -> Color(0xFFE57373)
                        selectedAnswer == option -> Color(0xFFB39DDB)
                        else -> Color.LightGray
                    }

                    Button(
                        onClick = {
                            if (!showAnswer) {
                                selectedAnswer = option
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Text(option)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (!showAnswer) {
                            showAnswer = true
                            if (selectedAnswer == currentQuestion.correctAnswer) {
                                score++
                            }
                        } else {
                            if (currentIndex < questionList.lastIndex) {
                                currentIndex++
                                selectedAnswer = null
                                showAnswer = false
                            } else {
                                navController.navigate("memory_score/$score")
                            }
                        }
                    },
                    enabled = selectedAnswer != null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB39DDB)
                    )
                ) {
                    Text(if (showAnswer) "NEXT" else "CHECK")
                }
            }
        }

        // 🔊 Read-aloud FAB
        SpeakerFab(
            textToRead = readText,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp)
        )
    }
}
