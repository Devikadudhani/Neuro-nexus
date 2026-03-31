package com.runanywhere.kotlin_starter_example.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.runanywhere.kotlin_starter_example.ui.components.CustomBottomBar
import com.runanywhere.kotlin_starter_example.ui.components.NeuroTopBar
import com.runanywhere.kotlin_starter_example.ui.components.SpeakerFab
import com.runanywhere.kotlin_starter_example.ui.theme.Ink
import com.runanywhere.kotlin_starter_example.ui.theme.LavenderShell
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info


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
                textToRead = if (state.isCompleted && state.result != null) "Your task is completed. Your score is ${state.result?.score}" else state.status,
                modifier = Modifier.padding(bottom = 0.dp)
            )
        },
        floatingActionButtonPosition = FabPosition.End
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
                text = "Reading Test",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            // Timer Countdown (Only during recording)
            if (state.isRecording) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                    modifier = Modifier.padding(vertical = 8.dp)
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

            // Status message (Shows "Task Completed" when done)
            Text(
                text = state.status,
                color = if (state.isCompleted) Color(0xFF2E7D32) else Ink,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = if (state.isCompleted) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.align(Alignment.Start)
            )

            // Live Transcript Box (Always visible if data exists)
            if (state.isRecording || state.transcript.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Transcript:", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (state.transcript.isEmpty()) "Start speaking..." else state.transcript,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Ink
                        )
                    }
                }
            }

            // Results Section with Calculated Parameters
            state.result?.let {
                // 1. ORIGINAL Analysis Results Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Analysis Results",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text("Acoustic Metrics", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        ParameterRow("Avg Amplitude (Volume)", "%.4f".format(it.features.averageAmplitude))
                        ParameterRow("Zero Crossing (Clarity)", "%.4f".format(it.features.zeroCrossingRate))
                        ParameterRow("Speaking Duration", "%.2f sec".format(it.features.speakingDurationSec))
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text("Linguistic Metrics", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        ParameterRow("Speech Rate", "%.1f WPM".format(it.features.speechRateWpm))
                        ParameterRow("Lexical Diversity", "%.2f".format(it.features.lexicalDiversity))
                        ParameterRow("Filler Word Count", "${it.features.fillerCount}")
                        ParameterRow("Repetition Ratio", "%.2f".format(it.features.repetitionRatio))
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Overall Score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("${it.score}/100", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                // 2. NEW Cognitive Analysis Card (Dementia Detection Layer)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = when (it.features.riskLevel) {
                            "High Risk Pattern" -> Color(0xFFFFEBEE)
                            "Mild Cognitive Concern" -> Color(0xFFFFF3E0)
                            else -> Color(0xFFE8F5E9)
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (it.features.riskScore > 0) Icons.Default.Warning else Icons.Default.Info,
                                contentDescription = null,
                                tint = when (it.features.riskLevel) {
                                    "High Risk Pattern" -> Color.Red
                                    "Mild Cognitive Concern" -> Color(0xFFEF6C00)
                                    else -> Color(0xFF2E7D32)
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cognitive Analysis",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Risk Level:", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = it.features.riskLevel,
                                fontWeight = FontWeight.ExtraBold,
                                color = when (it.features.riskLevel) {
                                    "High Risk Pattern" -> Color.Red
                                    "Mild Cognitive Concern" -> Color(0xFFEF6C00)
                                    else -> Color(0xFF2E7D32)
                                }
                            )
                        }
                        
                        ParameterRow("Risk Pattern Score", "${it.features.riskScore}")
                        
                        if (it.features.detectedIssues.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Detected Observations:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            it.features.detectedIssues.forEach { issue ->
                                Text(
                                    text = "• $issue",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Action Buttons
            if (!state.isCompleted) {
                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        } else {
                            if (state.isRecording) viewModel.stopTask() else viewModel.startTask()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.isRecording) Color.Red else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (state.isRecording) "Stop Recording" else "Start Reading Test",
                        color = Color.White
                    )
                }
            } else {
                Button(
                    onClick = { viewModel.resetTask() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Try Again", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ParameterRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Ink.copy(alpha = 0.7f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Ink)
    }
}
