package com.runanywhere.kotlin_starter_example.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.runanywhere.kotlin_starter_example.ui.components.NeuroTopBar
import com.runanywhere.kotlin_starter_example.ui.components.SpeakerFab
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun DualTestGameScreen(navController: NavController) {

    var tapCount by remember { mutableStateOf(0) }
    var currentNumber by remember { mutableStateOf(Random.nextInt(1,11)) }
    var score by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(true) }
    var time by remember { mutableStateOf(0) }
    var attempts by remember { mutableStateOf(0) }
    // timer
    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            time++
        }
    }

    val screenText = "Tap steadily and match the numbers at the same time."

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

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    "Tap steadily and match the numbers at the same time.",
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // TAP button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(150.dp)
                        .background(Color(0xFFB39DDB), CircleShape)
                        .clickable {
                            tapCount++
                        }
                ) {
                    Text("TAP", fontSize = 26.sp, color = Color.White)
                }

                // Number
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color(0xFFB39DDB), CircleShape)
                ) {
                    Text(
                        currentNumber.toString(),
                        fontSize = 28.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // keypad
            Column {
                for (row in 0..2) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        for (col in 1..3) {
                            val number = row * 3 + col

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                                    .clickable {

                                        attempts++

                                        if (number == currentNumber) {
                                            score++
                                        }

                                        if (attempts == 10) {
                                            navController.navigate("dual_test_result/$score")
                                        } else {
                                            currentNumber = Random.nextInt(1,10)
                                        }
                                    }
                            ) {
                                Text(
                                    number.toString(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                FloatingActionButton(
                    onClick = { isRunning = !isRunning },
                    containerColor = Color(0xFFB39DDB)
                ) {
                    Icon(Icons.Default.Pause, contentDescription = null)
                }

                Text(
                    String.format("%02d:%02d", time/60, time%60),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    navController.navigate("dual_test_result/$score/6")                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB39DDB)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("NEXT", fontSize = 18.sp)
            }
        }

        SpeakerFab(textToRead = screenText)
    }
}