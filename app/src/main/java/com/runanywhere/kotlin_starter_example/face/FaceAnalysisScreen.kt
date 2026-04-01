package com.runanywhere.kotlin_starter_example.face

import android.Manifest
import android.graphics.Bitmap
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.runanywhere.kotlin_starter_example.ui.components.CustomBottomBar
import com.runanywhere.kotlin_starter_example.ui.components.NeuroTopBar
import com.runanywhere.kotlin_starter_example.ui.theme.LavenderShell
import com.runanywhere.kotlin_starter_example.ui.theme.Ink
import com.runanywhere.kotlin_starter_example.model.FeatureResult
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceAnalysisScreen(
    navController: NavController,
    viewModel: FaceAnalysisViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(LavenderShell)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val localColumnScope = this
            
            Text(
                text = "Face Analysis",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            // Camera Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (hasCameraPermission) {
                    CameraXPreview(
                        modifier = Modifier.fillMaxSize(),
                        onFrame = { bitmap -> viewModel.onFrame(bitmap) }
                    )
                } else {
                    Text("Camera permission required", color = Color.White)
                }

                // --- ENGAGING OVERLAYS ---
                
                // 1. Smile Detection Indicator
                localColumnScope.AnimatedVisibility(
                    visible = state.isSmiling,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                ) {
                    Surface(
                        color = Color(0xFF669900),
                        shape = RoundedCornerShape(12.dp),
                        tonalElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.SentimentSatisfiedAlt, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("Smile Detected!", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // 2. Prompt Overlay
                state.currentPrompt?.let { prompt ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.Yellow,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Text(
                text = state.status,
                color = Ink,
                style = MaterialTheme.typography.titleMedium
            )

            // Results Section
            state.result?.let { res ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Detected Facial Cues",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        ResultRow(res.blinkRate)
                        ResultRow(res.expressiveness)
                        ResultRow(res.mouthMovement)
                        ResultRow(res.reactivity)
                        ResultRow(res.engagement)
                        ResultRow(res.headStability)
                        ResultRow(res.overallVariability)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "These results represent behavioral patterns observed during this session and are for screening purposes only.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            if (!state.isTestRunning) {
                Button(
                    onClick = { viewModel.startTest() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (state.result == null) "Start Test" else "Retake Test")
                }
            } else {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Text("Analyzing... please wait", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun ResultRow(result: FeatureResult) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = result.label, style = MaterialTheme.typography.bodyMedium, color = Ink)
        Surface(
            color = Color(result.color).copy(alpha = 0.15f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = result.value,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color(result.color)
            )
        }
    }
}

@Composable
fun CameraXPreview(
    modifier: Modifier,
    onFrame: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()

        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
            val bitmap = imageProxy.toBitmap()
            onFrame(bitmap)
            imageProxy.close()
        }

        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
        } catch (e: Exception) {
            Log.e("FaceAnalysis", "Camera binding failed", e)
        }
    }

    AndroidView(factory = { previewView }, modifier = modifier)
}
