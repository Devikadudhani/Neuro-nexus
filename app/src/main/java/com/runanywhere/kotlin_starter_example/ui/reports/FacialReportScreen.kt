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
import com.runanywhere.kotlin_starter_example.model.FeatureResult
import com.runanywhere.kotlin_starter_example.ui.components.CustomBottomBar
import com.runanywhere.kotlin_starter_example.ui.components.NeuroTopBar
import com.runanywhere.kotlin_starter_example.ui.theme.LavenderShell
import com.runanywhere.kotlin_starter_example.ui.theme.Ink

@Composable
fun FacialReportScreen(navController: NavController) {
    val faceData = AnalysisDataStore.faceData ?: return

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
                .background(Color(0xFFF3E5F5)) // Background Lilac shade
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Facial Analysis",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A148C)
                )
            }

            Text(text = "Detailed Clinical Markers:", fontSize = 18.sp, fontWeight = FontWeight.Medium)

            // Mapping results to the requested Progress Style
            ReportProgressItem("Blink Rate Pattern", faceData.blinkRate)
            ReportProgressItem("Expressiveness (Affect)", faceData.expressiveness)
            ReportProgressItem("Mouth & Speech Mobility", faceData.mouthMovement)
            ReportProgressItem("Upper Face Reactivity", faceData.reactivity)
            ReportProgressItem("Gaze & Attention", faceData.engagement)
            ReportProgressItem("Postural Head Stability", faceData.headStability)
            ReportProgressItem("Overall Affect Variance", faceData.overallVariability)

            Spacer(modifier = Modifier.height(8.dp))

            // Clinical Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Analysis Remarks",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A148C)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (faceData.detectedIssues.isEmpty()) {
                        Text(
                            text = "Observations indicate facial motor control and emotional reactivity are within standard ranges for this session.",
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            lineHeight = 20.sp
                        )
                    } else {
                        faceData.detectedIssues.forEach { issue ->
                            Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                                Icon(
                                    Icons.Default.Info, 
                                    contentDescription = null, 
                                    modifier = Modifier.size(16.dp).padding(top = 2.dp),
                                    tint = Color(0xFF7E57C2)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = issue,
                                    fontSize = 14.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            // --- NEW: Clinical Consultation Guide ---
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
                        Text(
                            text = "Next Steps & Consultation",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A148C)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "If you observe persistent changes in facial expressiveness or blinking, consider sharing this report with a neurologist or primary care physician.",
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { /* Could open a link or dialer */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E57C2)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Find a Specialist", fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Note: NeuroNexus uses algorithmic behavioral mapping. This is not a substitute for professional medical advice.",
                fontSize = 11.sp,
                color = Color.Gray,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun ReportProgressItem(label: String, result: FeatureResult) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF4A148C))
            
            val statusText = when(result.color) {
                0xFF669900L -> "Normal"
                0xFFFFBB33L -> "Caution"
                else -> "Alert"
            }
            Text(
                text = statusText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(result.color)
            )
        }
        Text(
            text = result.value,
            fontSize = 13.sp,
            color = Color.DarkGray.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        // Progress mapping: Alert (20%), Caution (50%), Normal (90%)
        val progress = when(result.color) {
            0xFF669900L -> 0.9f
            0xFFFFBB33L -> 0.5f
            else -> 0.2f
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp),
            color = Color(0xFFB39DDB), // Keep bar Lilac as requested
            trackColor = Color.White.copy(alpha = 0.4f),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}
