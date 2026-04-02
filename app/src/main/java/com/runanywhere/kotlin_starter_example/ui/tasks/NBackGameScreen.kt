package com.runanywhere.kotlin_starter_example.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.runanywhere.kotlin_starter_example.ui.components.NeuroTopBar
import com.runanywhere.kotlin_starter_example.ui.components.SpeakerFab
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun NBackGameScreen(navController: NavController) {

    val n = 2
    val totalMatchesRequired = 10
    val totalRounds = 20

    var sequence by remember { mutableStateOf(listOf<Int>()) }
    var activeCell by remember { mutableStateOf(-1) }
    var score by remember { mutableStateOf(0) }
    var round by remember { mutableStateOf(0) }

    var alreadyAnswered by remember { mutableStateOf(false) }

    // reaction time
    var startTime by remember { mutableStateOf(0L) }
    var reactionTimes by remember { mutableStateOf(listOf<Long>()) }

    // ---------- generate fair sequence ----------
    LaunchedEffect(Unit) {

        val seq = MutableList(totalRounds) { Random.nextInt(0, 9) }

        val matchPositions =
            (n until totalRounds).shuffled().take(totalMatchesRequired)

        matchPositions.forEach { i ->
            seq[i] = seq[i - n]
        }

        sequence = seq
    }

    // ---------- run game ----------
    LaunchedEffect(sequence) {

        while (round < sequence.size) {

            activeCell = sequence[round]
            alreadyAnswered = false
            startTime = System.currentTimeMillis()

            delay(1200)

            activeCell = -1
            delay(400)

            round++
        }

        val avgReactionTime =
            if (reactionTimes.isNotEmpty())
                reactionTimes.average().toFloat() / 1000f
            else 0f

        navController.navigate(
            "nback_result/$score/${10 - score}/$avgReactionTime"
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F1F8))
    ) {

        Column {

            NeuroTopBar(navController)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .background(
                            Color(0xFFB39DDB),
                            RoundedCornerShape(50)
                        )
                        .padding(horizontal = 40.dp, vertical = 10.dp)
                ) {
                    Text(
                        "2-Back",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEDE7F6)
                    )
                ) {
                    Text(
                        "Watch the square on the grid. If it appears in the same spot as two turns ago, tap MATCH.",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .border(
                            3.dp,
                            Color(0xFF2196F3),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(10.dp)
                ) {

                    Column {
                        for (row in 0..2) {
                            Row {
                                for (col in 0..2) {

                                    val index = row * 3 + col

                                    Box(
                                        modifier = Modifier
                                            .size(90.dp)
                                            .padding(6.dp)
                                            .background(
                                                if (index == activeCell)
                                                    Color(0xFF81C784)
                                                else
                                                    Color(0xFFE0E0E0),
                                                RoundedCornerShape(6.dp)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {

                        if (!alreadyAnswered && round > n) {

                            alreadyAnswered = true

                            val reaction =
                                System.currentTimeMillis() - startTime

                            reactionTimes =
                                reactionTimes + reaction

                            val isMatch =
                                sequence[round - 1] ==
                                        sequence[round - 1 - n]

                            if (isMatch) score++
                        }
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB39DDB)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Text(
                        "MATCH",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Score: $score / 10",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        SpeakerFab(
            textToRead = "Match the square with two steps back",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}