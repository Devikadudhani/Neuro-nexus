package com.runanywhere.kotlin_starter_example.tasks

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import kotlinx.coroutines.delay
import com.runanywhere.kotlin_starter_example.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalConfiguration
import com.runanywhere.kotlin_starter_example.ui.components.NeuroTopBar
import android.speech.tts.TextToSpeech
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import androidx.compose.runtime.DisposableEffect



data class MemoryItem(
    val imageRes: Int,
    val name: String
)

@Composable
fun MemoryPreviewScreen(navController: NavController) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val context = LocalContext.current
    var textToSpeech by remember { mutableStateOf<TextToSpeech?>(null) }


    val memoryData = listOf(
        MemoryItem(R.drawable.person_ross, "Ross"),
        MemoryItem(R.drawable.person_rachel, "Rachel"),
        MemoryItem(R.drawable.person_monica, "Monica"),
        MemoryItem(R.drawable.person_chandler, "Chandler")
    )
    val memoryList = listOf(
        MemoryItem(R.drawable.person_ross, "Ross"),
        MemoryItem(R.drawable.person_rachel, "Rachel"),
        MemoryItem(R.drawable.person_monica, "Monica"),
        MemoryItem(R.drawable.person_chandler, "Chandler")
    )


    var currentIndex by remember { mutableStateOf(0) }

    // Auto change every 3 seconds
    LaunchedEffect(currentIndex) {

        // 🔊 AUTO SPEAK WHEN CARD APPEARS
        textToSpeech?.speak(
            memoryList[currentIndex].name,
            TextToSpeech.QUEUE_FLUSH,
            null,
            null
        )

        delay(4000)

        if (currentIndex < memoryList.lastIndex) {
            currentIndex++
        }
        else {
            // ✅ LAST IMAGE REACHED → NAVIGATE
            navController.navigate("memory_recall") {
                popUpTo("memory_preview") { inclusive = true }
            }
        }

    }

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


    val progress by animateFloatAsState(
        targetValue = (currentIndex + 1) / memoryList.size.toFloat(),
        label = ""
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F1F8))
    ) {

        // 🔹 YOUR TOP BAR
        NeuroTopBar()

        Spacer(modifier = Modifier.height(30.dp))

        val item = memoryList[currentIndex]

        // 🔹 Profile Image
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 🔹 Card Box
        Card(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
            .height(screenHeight * 0.28f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE0E0E0)
            )
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = item.name,
                    fontSize = 28.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color(0xFFB39DDB),
                    trackColor = Color(0xFFD1C4E9)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${currentIndex + 1} of ${memoryList.size}",
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Remember this pair",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 18.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                textToSpeech?.speak(
                    memoryList[currentIndex].name,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )
            },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB39DDB)
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Read Aloud")
        }

    }
}
