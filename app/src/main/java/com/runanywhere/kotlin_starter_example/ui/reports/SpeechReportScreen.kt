package com.runanywhere.kotlin_starter_example.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.runanywhere.kotlin_starter_example.data.AnalysisDataStore
import com.runanywhere.kotlin_starter_example.ui.components.CustomBottomBar
import com.runanywhere.kotlin_starter_example.ui.components.NeuroTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeechReportScreen(navController: NavController) {
    val data = AnalysisDataStore.voiceData ?: return

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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF3E5F5)) // Lilac shade
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Speech Analysis",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A148C)
                )
            }

            Text(text = "Clinical Markers Profile:", fontSize = 18.sp, fontWeight = FontWeight.Medium)

            // Acoustic Markers
            SpeechMetricItem("Speech Rate (WPM)", "${data.speechRateWpm.toInt()} WPM", (data.speechRateWpm.toFloat() / 180f).coerceIn(0.2f, 0.9f) * 100f, getStatus(data.speechRateWpm, 100.0, 180.0, true))
            SpeechMetricItem("Pause Frequency", "${data.pauseRate.toInt()} per min", (1.0f - (data.pauseRate.toFloat() / 20f)).coerceIn(0.2f, 0.9f) * 100f, getStatus(data.pauseRate, 0.0, 10.0, false))
            
            // Linguistic Markers
            SpeechMetricItem("Lexical Diversity", "${(data.lexicalDiversity * 100).toInt()}%", (data.lexicalDiversity.toFloat() * 100).coerceIn(20f, 90f), getStatus(data.lexicalDiversity, 0.5, 1.0, true))
            SpeechMetricItem("Semantic Coherence", "${(data.coherenceScore * 100).toInt()}%", (data.coherenceScore.toFloat() * 100).coerceIn(20f, 90f), getStatus(data.coherenceScore, 0.4, 1.0, true))
            
            // Energy
            SpeechMetricItem("Vocal Energy (RMS)", "${(data.rmsEnergy * 100).toInt()}%", (data.rmsEnergy.toFloat() * 100).coerceIn(20f, 90f), "Normal")

            Spacer(modifier = Modifier.height(8.dp))

            // Clinical Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Clinical Observations",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A148C)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val aiInsight = data.aiInsight ?: data.riskLevel
                    Text(
                        text = if (aiInsight.isNotBlank() && aiInsight != "Normal") aiInsight else "Acoustic and linguistic patterns appear stable for this session.",
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                    
                    if (data.detectedIssues.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        data.detectedIssues.forEach { issue ->
                            Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp).padding(top = 2.dp), tint = Color(0xFF7E57C2))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = issue, fontSize = 14.sp, color = Color.DarkGray)
                            }
                        }
                    }
                }
            }

            // Reference Values Card for Specialists
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB39DDB))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color(0xFF4A148C))
                        Spacer(Modifier.width(8.dp))
                        Text("Specialist Reference Data", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4A148C))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    ReferenceDataRow("Mean Pause Duration", "%.2f s".format(data.meanPauseDuration))
                    ReferenceDataRow("MATTR Diversity", "%.3f".format(data.lexicalDiversity))
                    ReferenceDataRow("Syntactic Variance", "%.2f".format(data.sentenceLengthVariance))
                    ReferenceDataRow("Pronoun Ratio", "%.2f".format(data.pronounRatio))
                    ReferenceDataRow("Pause-to-Speech", "%.2f".format(data.pauseToSpeechRatio))

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { /* call */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E57C2)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Connect with Clinical Specialist")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SpeechMetricItem(label: String, value: String, score: Float, status: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF4A148C))
            Text(text = status, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = when(status) {
                "Normal" -> Color(0xFF669900)
                "Caution" -> Color(0xFFFFBB33)
                else -> Color(0xFFFF4444)
            })
        }
        Text(text = value, fontSize = 13.sp, color = Color.DarkGray.copy(alpha = 0.7f), modifier = Modifier.padding(bottom = 4.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier.fillMaxWidth().height(12.dp),
            color = Color(0xFFB39DDB),
            trackColor = Color.White.copy(alpha = 0.4f),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}

@Composable
fun ReferenceDataRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, fontSize = 13.sp, color = Color.Gray)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4A148C))
    }
}

private fun getStatus(value: Double, low: Double, high: Double, higherIsBetter: Boolean): String {
    return if (higherIsBetter) {
        when {
            value >= high -> "Normal"
            value >= low -> "Caution"
            else -> "Alert"
        }
    } else {
        when {
            value <= low -> "Normal"
            value <= high -> "Caution"
            else -> "Alert"
        }
    }
}
