package com.runanywhere.kotlin_starter_example.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.runanywhere.kotlin_starter_example.ui.components.CustomBottomBar
import com.runanywhere.kotlin_starter_example.ui.components.NeuroTopBar
import com.runanywhere.kotlin_starter_example.ui.components.SpeakerFab
import com.runanywhere.kotlin_starter_example.ui.theme.Ink
import com.runanywhere.kotlin_starter_example.ui.theme.LavenderShell

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceTaskScreen(
    navController: NavController,
    viewModel: VoiceTaskViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.startTask()
    }

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
                textToRead = if (state.isCompleted && state.result != null) "Task completed. Viewing speech analysis markers." else state.status
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(LavenderShell)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Speech Analysis Task",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start),
                color = Color(0xFF4A148C)
            )

            if (state.isRecording) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = "00:${state.timerSeconds.toString().padStart(2, '0')}",
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }
            }

            Text(
                text = state.status,
                color = if (state.isCompleted) Color(0xFF2E7D32) else Ink,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (state.isCompleted) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.align(Alignment.Start)
            )

            if (state.isRecording || state.transcript.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Transcription:", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (state.transcript.isEmpty()) "Monitoring audio..." else state.transcript,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Ink
                        )
                    }
                }
            }

            // Results Section (Dynamic Markers)
            state.result?.let { res ->
                Text(
                    text = "Acoustic & Linguistic Profile",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start),
                    color = Color(0xFF4A148C)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        VoiceParameterRow("Speech Rate", "${res.features.speechRateWpm.toInt()} WPM")
                        VoiceParameterRow("Pause Rate", "%.1f / min".format(res.features.pauseRate))
                        VoiceParameterRow("Lexical Diversity", "%.2f".format(res.features.lexicalDiversity))
                        VoiceParameterRow("Semantic Coherence", "%.2f".format(res.features.coherenceScore))
                        VoiceParameterRow("Vocal Energy (RMS)", "%.3f".format(res.features.rmsEnergy))
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Overall Fluency Score", fontWeight = FontWeight.Bold)
                            Text("${res.score}/100", fontWeight = FontWeight.ExtraBold, color = Color(0xFF7E57C2))
                        }
                    }
                }

                // LLM Observation Remark
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF7E57C2))
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = if (res.features.riskLevel != "Normal" && res.features.riskLevel.isNotBlank()) res.features.riskLevel else "Speech patterns are within normal variance for this task.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            if (!state.isCompleted) {
                Button(
                    onClick = {
                        val permission = Manifest.permission.RECORD_AUDIO
                        if (ContextCompat.checkSelfPermission(context, permission) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                            if (state.isRecording) viewModel.stopTask() else viewModel.startTask()
                        } else {
                            permissionLauncher.launch(permission)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = if (state.isRecording) Color.Red else MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (state.isRecording) "Finish Task" else "Begin Reading Test")
                }
            } else {
                Button(
                    onClick = { viewModel.resetTask() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Restart Task")
                }
                
                OutlinedButton(
                    onClick = { navController.navigate("speech_report") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("View Detailed Report")
                }
            }
        }
    }
}

@Composable
fun VoiceParameterRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp)
        Text(text = value, fontWeight = FontWeight.Bold, color = Ink, fontSize = 14.sp)
    }
}
