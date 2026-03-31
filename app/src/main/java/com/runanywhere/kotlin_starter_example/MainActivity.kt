package com.runanywhere.kotlin_starter_example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.runanywhere.kotlin_starter_example.services.ModelService
import com.runanywhere.kotlin_starter_example.ui.theme.NeuroNexusTheme
import android.util.Log
import androidx.activity.viewModels
import com.runanywhere.sdk.core.onnx.ONNX
import com.runanywhere.sdk.foundation.bridge.extensions.CppBridgeModelPaths
import com.runanywhere.sdk.llm.llamacpp.LlamaCPP
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.public.SDKEnvironment
import com.runanywhere.sdk.storage.AndroidPlatformContext
import com.runanywhere.kotlin_starter_example.ui.auth.*
import com.runanywhere.kotlin_starter_example.ui.dashboard.NeuroNexusDashboard
import com.runanywhere.kotlin_starter_example.ui.tasks.*
import com.runanywhere.kotlin_starter_example.community.CommunityPage
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.runanywhere.kotlin_starter_example.tasks.MemoryPreviewScreen
import com.runanywhere.kotlin_starter_example.ui.VoiceTaskScreen
import com.runanywhere.kotlin_starter_example.ui.VoiceTaskViewModel
import com.runanywhere.kotlin_starter_example.ui.profile.ProfileScreen
import com.runanywhere.kotlin_starter_example.ui.tasks.RecallQuestionScreen
import com.runanywhere.kotlin_starter_example.ui.tasks.RecallResultScreen

class MainActivity : ComponentActivity() {

    private val viewModel: VoiceTaskViewModel by viewModels {
        VoiceTaskViewModel.Factory(application)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize Android platform context FIRST - this sets up storage paths
        // The SDK requires this before RunAnywhere.initialize() on Android
        AndroidPlatformContext.initialize(this)
        
        // Initialize RunAnywhere SDK for development
        RunAnywhere.initialize(environment = SDKEnvironment.DEVELOPMENT)
        
        // Set the base directory for model storage
        val runanywherePath = java.io.File(filesDir, "runanywhere").absolutePath
        CppBridgeModelPaths.setBaseDirectory(runanywherePath)

        // Register backends FIRST - these must be registered before loading any models
        // They provide the inference capabilities (TEXT_GENERATION, STT, TTS, VLM)
        try {
            LlamaCPP.register(priority = 100)  // For LLM + VLM (GGUF models)
        } catch (e: Throwable) {
            // VLM native registration may fail if .so doesn't include nativeRegisterVlm;
            // LLM text generation still works since it was registered before VLM in register()
            Log.w("MainActivity", "LlamaCPP.register partial failure (VLM may be unavailable): ${e.message}")
        }
        ONNX.register(priority = 100)      // For STT/TTS (ONNX models)
        
        // Register default models
        ModelService.registerDefaultModels()
        
        setContent {
            NeuroNexusTheme {
                RunAnywhereApp()
            }
        }
    }
}

@Composable
fun RunAnywhereApp() {
    val navController = rememberNavController()
//    val modelService: ModelService = viewModel()
    
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {

        // -------- AUTH --------
        composable("welcome") { WelcomeScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("role_select") { RoleSelectionScreen(navController) }
        composable("signup_personal") { SignupPersonalScreen(navController) }
        composable("signup_contact") { SignupContactScreen(navController) }
        composable("signup_health") { SignupHealthScreen(navController) }
        composable("signup_insurance") { SignupInsuranceScreen(navController) }
        composable("signup_profile") { SignupProfileScreen(navController) }
        composable("avatar_select") { AvatarSelectionScreen(navController) }
        composable("loading") { LoadingScreen(navController) }

// -------- DASHBOARD --------
        composable("dashboard") {
            NeuroNexusDashboard(
                navController = navController,
                onHomeClick = { navController.navigate("dashboard") },
                onTasksClick = { navController.navigate("tasks") },
                onSettingsClick = {},
                onShareClick = { navController.navigate("community") }
            )
        }

// -------- TASKS --------
        composable("tasks") { TasksScreen(navController) }
        composable("memory_match") { MemoryMatchScreen(navController) }
        composable("memory_mcq") { MemoryMcqScreen(navController) }

// -------- PROFILE --------
        composable("profile") { ProfileScreen(navController) }

// -------- COMMUNITY --------
        composable("community") { CommunityPage(navController) }

        composable("voice_task") {
            VoiceTaskScreen(
                navController = navController,
                viewModel = viewModel()
            )
        }

        composable("narrative_recall") {
            NarrativeRecallScreen(navController)
        }

        composable("memory_preview") {
            MemoryPreviewScreen(navController)
        }

        composable("memory_recall") {
            MemoryRecallScreen(navController)
        }

        composable("story") {
            StoryScreen(navController)
        }

        composable("recall_phase") {
            RecallPhaseScreen(navController)
        }

        composable("recall_question") {
            RecallQuestionScreen(navController)
        }
        composable("recall_result/{score}/{time}") { backStackEntry ->

            val score = backStackEntry.arguments
                ?.getString("score")
                ?.toIntOrNull() ?: 0

            val time = backStackEntry.arguments
                ?.getString("time")
                ?.toIntOrNull() ?: 0

            RecallResultScreen(navController, score, time)
        }

        composable("memory_mcq") {
            MemoryMcqScreen(navController)
        }

        composable(
            route = "memory_score/{score}",
            arguments = listOf(navArgument("score") { type = NavType.IntType })
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            MemoryScoreScreen(navController, score)
        }

        composable("stroop_intro") {
            StroopIntroScreen(navController)
        }

        composable("stroop_game") {
            StroopGameScreen(navController)
        }

        composable("stroop_result/{score}/{time}") { backStackEntry ->
            val score = backStackEntry.arguments?.getString("score")?.toInt() ?: 0
            val time = backStackEntry.arguments?.getString("time")?.toInt() ?: 0
            StroopResultScreen(navController, score, time)
        }
    }
}
