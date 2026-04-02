package com.runanywhere.kotlin_starter_example.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.runanywhere.kotlin_starter_example.R
import com.runanywhere.kotlin_starter_example.services.ModelService
import com.runanywhere.kotlin_starter_example.ui.components.CustomBottomBar
import com.runanywhere.kotlin_starter_example.ui.components.ModelLoaderWidget
import com.runanywhere.kotlin_starter_example.ui.components.NeuroTopBar
import com.runanywhere.kotlin_starter_example.ui.components.SpeakerFab

@Composable
fun TasksScreen(
    navController: NavController,
    tasksText: String = stringResource(R.string.tasks)
) {
    val modelService: ModelService = viewModel()

    Scaffold(
        topBar = { NeuroTopBar(navController) },
        bottomBar = {
            CustomBottomBar(
                navController = navController,
                onHomeClick = { navController.navigate("dashboard") },
                onTasksClick = { navController.navigate("tasks") },
                onSettingsClick = { navController.navigate("settings") },
                onShareClick = { navController.navigate("community") }
            )
        },
        floatingActionButton = {
            SpeakerFab(
                textToRead = tasksText,
                modifier = Modifier.padding(bottom = 0.dp)
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF3E5F5))
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = stringResource(R.string.model_management),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                ModelLoaderWidget(
                    modelName = stringResource(R.string.language_model),
                    isDownloading = modelService.isLLMDownloading,
                    isLoading = modelService.isLLMLoading,
                    isLoaded = modelService.isLLMLoaded,
                    downloadProgress = modelService.llmDownloadProgress,
                    onLoadClick = { modelService.downloadAndLoadLLM() }
                )

                ModelLoaderWidget(
                    modelName = stringResource(R.string.speech_recognition),
                    isDownloading = modelService.isSTTDownloading,
                    isLoading = modelService.isSTTLoading,
                    isLoaded = modelService.isSTTLoaded,
                    downloadProgress = modelService.sttDownloadProgress,
                    onLoadClick = { modelService.downloadAndLoadSTT() }
                )

                ModelLoaderWidget(
                    modelName = stringResource(R.string.text_to_speech_model),
                    isDownloading = modelService.isTTSDownloading,
                    isLoading = modelService.isTTSLoading,
                    isLoaded = modelService.isTTSLoaded,
                    downloadProgress = modelService.ttsDownloadProgress,
                    onLoadClick = { modelService.downloadAndLoadTTS() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.tasks),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                TaskItem(
                    title = stringResource(R.string.memory_match),
                    description = "Learn picture–name pairs, then match them correctly.",
                    bgColor = Color(0xFFE9D5FF),
                    onStartClick = { navController.navigate("memory_match") }
                )

                TaskItem(
                    title = stringResource(R.string.stroop_test),
                    description = "Name the ink color of words that spell different colors.",
                    bgColor = Color(0xFFE9D5FF),
                    onStartClick = { navController.navigate("stroop_intro") }
                )

                TaskItem(
                    title = stringResource(R.string.narrative_recall),
                    description = "Listen to a short story and later recall its details.",
                    bgColor = Color(0xFFD1E9FF),
                    onStartClick = { navController.navigate("story") }
                )

                TaskItem(
                    title = stringResource(R.string.reading_test),
                    description = "Read a paragraph on screen at your own pace.",
                    bgColor = Color(0xFFD1FADF),
                    onStartClick = { navController.navigate("voice_task") }
                )

                TaskItem(
                    title = "Dual Test",
                    description = "Perform two tasks at once (e.g., tapping and counting).",
                    bgColor = Color(0xFFD1FADF),
                    onStartClick = { navController.navigate("dual_test_intro") }
                )

                TaskItem(
                    title = "N-Back Task",
                    description = "Watch the sequence and tap when it matches N steps ago.",
                    bgColor = Color(0xFFE9D5FF),
                    onStartClick = { navController.navigate("nback_intro") }
                )

                TaskItem(
                    title = "Face Capture",
                    description = "Record facial expressions and subtle reactions.",
                    bgColor = Color(0xFFFFF4C2),
                    onStartClick = { navController.navigate("face_analysis") }
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    title: String,
    description: String,
    bgColor: Color,
    onStartClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D2939)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF475467)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                Button(
                    onClick = onStartClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(50),
                    elevation = ButtonDefaults.buttonElevation(2.dp)
                ) {
                    Text("Start Activity", color = Color.Black)
                }

                OutlinedButton(
                    onClick = { },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                ) {
                    Text("Demo Video")
                }
            }
        }
    }
}