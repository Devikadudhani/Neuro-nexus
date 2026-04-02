package com.runanywhere.kotlin_starter_example.ui.tasks

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.runanywhere.kotlin_starter_example.R
import com.runanywhere.kotlin_starter_example.ui.components.NeuroTopBar
import com.runanywhere.kotlin_starter_example.ui.components.SpeakerFab
@Composable
fun NBackResultScreen(
    navController: NavController,
    score: Int,
    errors: Int,
    avgTime: Float
) {

    val screenText =
        "Great! You completed this task. Your score is $score out of 10."

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD9C8E9))
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            NeuroTopBar(navController)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(Color(0xFFF7F7F7))
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                // Speech bubble
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE6EBF0)
                        ),
                        modifier = Modifier.width(230.dp)
                    ) {
                        Text(
                            "Great!!\nYou have\ncompleted this\ntask",
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF444444),
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mascot + score
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.mascot_img),
                        contentDescription = null,
                        modifier = Modifier
                            .height(330.dp)
                            .weight(1.2f),
                        contentScale = ContentScale.Fit
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(140.dp)
                            .background(Color(0xFFB39DDB), CircleShape)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Score",
                                color = Color.White,
                                fontSize = 20.sp
                            )
                            Text(
                                "$score/10",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Errors
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "✕",
                        color = Color(0xFFD32F2F),
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "$errors Errors",
                        color = Color(0xFFD32F2F),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    "Average\nReaction\nTime- ${String.format("%.1f", avgTime)}s",
                    textAlign = TextAlign.Center,
                    color = Color(0xFF6A4FB3),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Keep practising to improve\n your performance",
                    textAlign = TextAlign.Center,
                    color = Color(0xFF555555),
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = { navController.navigate("tasks") },
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB39DDB)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                ) {
                    Text(
                        "NEXT",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        SpeakerFab(
            textToRead = screenText,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}