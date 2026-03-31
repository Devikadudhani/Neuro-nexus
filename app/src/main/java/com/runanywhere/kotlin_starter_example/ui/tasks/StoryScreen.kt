package com.runanywhere.kotlin_starter_example.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.runanywhere.kotlin_starter_example.tts.TtsController
import com.runanywhere.kotlin_starter_example.ui.components.CustomBottomBar
import com.runanywhere.kotlin_starter_example.ui.components.NeuroTopBar
@Composable
fun StoryScreen(navController: NavController) {

    val context = LocalContext.current
    val ttsController = remember { TtsController(context) }

    DisposableEffect(Unit) {
        onDispose {
            ttsController.shutdown()
        }
    }

    val storyText = "One evening, a little girl named Riya found a small puppy near the park. The puppy was shivering because it was cold. Riya gave the puppy some biscuits and carried it home. Her mother agreed to let the puppy stay until they found its owner. The next day, they put up posters around the neighborhood. By evening, a young boy came and happily hugged the puppy — it was his lost pet named Bruno. Riya felt both sad and happy, knowing the puppy was back with its family."

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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "The Lost Puppy",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A4A4A)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = storyText,
                    modifier = Modifier.padding(18.dp),
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Remember the story",
                fontSize = 15.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Read Aloud button
            Button(
                onClick = {
                    ttsController.speak(storyText)
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB39DDB)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Read Aloud",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 17.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Done button
            Button(
                onClick = {
                    navController.navigate("recall_phase")
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8E6BAF)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Done",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 17.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        CustomBottomBar(
            navController = navController,
            onHomeClick = { navController.navigate("dashboard") },
            onTasksClick = { navController.navigate("tasks") },
            onSettingsClick = {navController.navigate("settings") },
            onShareClick = { navController.navigate("community") }
        )
    }
}
