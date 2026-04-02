package com.runanywhere.kotlin_starter_example.ui.tasks

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.runanywhere.kotlin_starter_example.ui.components.NeuroTopBar
import com.runanywhere.kotlin_starter_example.ui.components.SpeakerFab

@Composable
fun DualTestIntroScreen(navController: NavController) {

    val screenText =
        "Tap the big button steadily while numbers appear on the screen. Match each number by pressing it on the keypad keep tapping and responding at the same time."

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F1F8))
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            NeuroTopBar(navController)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "DUAL TEST",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B4E8E)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEAF6))
                ) {
                    Text(
                        text = screenText,
                        modifier = Modifier.padding(18.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                ) {

                    val boxWidth = maxWidth
                    val boxHeight = maxHeight

                    Image(
                        painter = painterResource(id = R.drawable.mascot_img),
                        contentDescription = "Mascot",
                        modifier = Modifier
                            .size(boxWidth * 1.1f)
                            .align(Alignment.BottomStart)
                            .offset(
                                x = boxWidth * (-0.08f),
                                y = boxHeight * (-0.05f)
                            ),
                        contentScale = ContentScale.Crop
                    )

                    Button(
                        onClick = {
                            navController.navigate("dual_test_game")
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB39DDB)
                        ),
                        modifier = Modifier
                            .height(60.dp)
                            .width(200.dp)
                            .align(Alignment.CenterEnd)
                            .offset(y = boxHeight * (-0.25f))
                            .padding(end = 20.dp)
                    ) {
                        Text(
                            text = "START",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        SpeakerFab(
            textToRead = screenText,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 24.dp)
        )
    }
}